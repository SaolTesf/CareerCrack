import React, { useEffect, useMemo, useRef, useState } from 'react';
import {
  useReactTable, getCoreRowModel, getSortedRowModel, getPaginationRowModel, flexRender,
} from '@tanstack/react-table';
import {
  FaChevronDown, FaChevronRight, FaExternalLinkAlt, FaPlus, FaSearch, FaTrashAlt,
} from 'react-icons/fa';
import { CATEGORIES } from '../../services/problemCategoryService';

const STATUS_OPTIONS = [
  ['TODO', 'To do'], ['ATTEMPTED', 'Attempted'], ['REVIEW', 'Review'],
  ['SOLVED', 'Solved'], ['MASTERED', 'Mastered'],
].map(([value, label]) => ({ value, label }));
const DIFFICULTY_OPTIONS = ['easy', 'medium', 'hard', 'hardcore']
  .map((value) => ({ value, label: value[0].toUpperCase() + value.slice(1) }));
const CATEGORY_OPTIONS = CATEGORIES.map((c) => ({ value: c.name, label: c.label }));

const labelOf = (options, value) => options.find((o) => o.value === value)?.label ?? value;
const rankSort = (options) => (a, b, id) =>
  options.findIndex((o) => o.value === a.getValue(id)) - options.findIndex((o) => o.value === b.getValue(id));

const Badge = ({ prefix, value, options }) => (
  <span className={`pr-badge ${prefix}-${String(value).toLowerCase()}`}>{labelOf(options, value)}</span>
);

const Options = ({ options }) => options.map((o) => <option key={o.value} value={o.value}>{o.label}</option>);

// Click a cell to edit it. Enter saves, Escape cancels. Selects save on change.
const EditableCell = ({ value, onSave, options, render }) => {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(value ?? '');
  const cancelled = useRef(false);

  const start = () => { cancelled.current = false; setDraft(value ?? ''); setEditing(true); };
  const commit = () => {
    setEditing(false);
    const v = draft.trim();
    if (!cancelled.current && v && v !== value) onSave(v);
  };

  if (!editing) {
    return (
      <button type="button" className="pr-cell" onClick={start} title="Click to edit">
        {render ? render(value) : value}
      </button>
    );
  }
  if (options) {
    return (
      <select
        className="pr-field" autoFocus value={draft}
        onChange={(e) => { setEditing(false); if (e.target.value !== value) onSave(e.target.value); }}
        onBlur={() => setEditing(false)}
      >
        <Options options={options} />
      </select>
    );
  }
  return (
    <input
      className="pr-field" autoFocus value={draft}
      onChange={(e) => setDraft(e.target.value)}
      onBlur={commit}
      onKeyDown={(e) => {
        if (e.key === 'Enter') e.currentTarget.blur();
        if (e.key === 'Escape') { cancelled.current = true; e.currentTarget.blur(); }
      }}
    />
  );
};

// Two-step delete: first click arms it, second click confirms. Disarms after 3 seconds.
const DeleteButton = ({ onConfirm }) => {
  const [armed, setArmed] = useState(false);
  useEffect(() => {
    if (!armed) return undefined;
    const t = setTimeout(() => setArmed(false), 3000);
    return () => clearTimeout(t);
  }, [armed]);
  return (
    <button
      type="button" aria-label="Delete problem"
      className={`pr-icon danger ${armed ? 'armed' : ''}`}
      onClick={() => (armed ? onConfirm() : setArmed(true))}
    >
      {armed ? 'Delete?' : <FaTrashAlt />}
    </button>
  );
};

// Tags, link, description and solution for one problem.
const DetailPanel = ({ problem, onSave, onDeleteProblemTag }) => {
  const [form, setForm] = useState({
    externalLink: problem.externalLink ?? '',
    description: problem.description ?? '',
    solution: problem.solution ?? '',
  });
  const [tagInput, setTagInput] = useState('');
  const tags = problem.tags ?? [];
  const dirty = Object.keys(form).some((k) => form[k] !== (problem[k] ?? ''));
  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const addTag = () => {
    const t = tagInput.trim();
    setTagInput('');
    if (t && !tags.some((x) => x.toLowerCase() === t.toLowerCase())) onSave({ tags: [...tags, t] });
  };

  return (
    <div className="pr-detail">
      <div className="wide">
        <h4>Tags</h4>
        <div className="pr-tag-input">
          {tags.map((t) => (
            <span key={t} className="pr-tag">
              {t}<button type="button" aria-label={`Remove tag ${t}`} onClick={() => onDeleteProblemTag(t)}>×</button>
            </span>
          ))}
          <input
            className="pr-field" list="pr-tag-options" placeholder="Add a tag, press Enter"
            value={tagInput} onChange={(e) => setTagInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && addTag()}
          />
        </div>
      </div>
      <div className="wide">
        <h4>Link</h4>
        <input className="pr-field" placeholder="https://" value={form.externalLink} onChange={set('externalLink')} />
      </div>
      <div>
        <h4>Description</h4>
        <textarea className="pr-field" placeholder="What is the problem asking?" value={form.description} onChange={set('description')} />
      </div>
      <div>
        <h4>Solution and notes</h4>
        <textarea className="pr-field mono" placeholder="Approach, complexity, code..." value={form.solution} onChange={set('solution')} />
      </div>
      <div className="wide pr-detail-actions">
        <button
          type="button" className="pr-btn primary" disabled={!dirty}
          onClick={() => onSave({ ...form, externalLink: form.externalLink.trim() || null })}
        >
          Save changes
        </button>
        {problem.updatedAt && <span className="pr-muted">Last updated {new Date(problem.updatedAt).toLocaleString()}</span>}
      </div>
    </div>
  );
};

// First row of the table: type a title and press Enter to add a problem.
const QuickAddRow = ({ onAdd, onCreated }) => {
  const [draft, setDraft] = useState({ title: '', categoryName: 'LEETCODE', difficulty: 'easy', status: 'TODO' });
  const [saving, setSaving] = useState(false);
  const set = (k) => (e) => setDraft((d) => ({ ...d, [k]: e.target.value }));

  const submit = async () => {
    if (!draft.title.trim() || saving) return;
    setSaving(true);
    const created = await onAdd({ ...draft, title: draft.title.trim() });
    setSaving(false);
    if (created) {
      setDraft((d) => ({ ...d, title: '' }));
      onCreated(created.id);
    }
  };

  return (
    <tr className="pr-add-row">
      <td><FaPlus aria-hidden="true" /></td>
      <td>
        <input
          className="pr-field" placeholder="New problem title, press Enter" aria-label="New problem title"
          value={draft.title} onChange={set('title')} onKeyDown={(e) => e.key === 'Enter' && submit()}
        />
      </td>
      <td><select className="pr-field" aria-label="Category" value={draft.categoryName} onChange={set('categoryName')}><Options options={CATEGORY_OPTIONS} /></select></td>
      <td><select className="pr-field" aria-label="Difficulty" value={draft.difficulty} onChange={set('difficulty')}><Options options={DIFFICULTY_OPTIONS} /></select></td>
      <td><select className="pr-field" aria-label="Status" value={draft.status} onChange={set('status')}><Options options={STATUS_OPTIONS} /></select></td>
      <td className="pr-muted">Add tags after creating</td>
      <td><button type="button" className="pr-btn primary" onClick={submit} disabled={saving || !draft.title.trim()}>Add</button></td>
    </tr>
  );
};

export const ProblemsTable = ({ problems, loading, onAdd, onUpdate, onDelete, onDeleteProblemTag }) => {
  const [filters, setFilters] = useState({ q: '', status: '', categoryName: '', difficulty: '' });
  const [sorting, setSorting] = useState([]);
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 });
  const [expandedId, setExpandedId] = useState(null);

  const data = useMemo(() => {
    const q = filters.q.trim().toLowerCase();
    return problems.filter((p) =>
      (!filters.status || p.status === filters.status) &&
      (!filters.categoryName || p.categoryName === filters.categoryName) &&
      (!filters.difficulty || p.difficulty === filters.difficulty) &&
      (!q || [p.title, p.categoryName, ...(p.tags ?? [])].some((s) => s?.toLowerCase().includes(q))));
  }, [problems, filters]);

  const columns = useMemo(() => [
    {
      id: 'expand',
      header: '',
      cell: ({ row, table }) => {
        const { expandedId: open, toggle } = table.options.meta;
        const isOpen = open === row.original.id;
        return (
          <button
            type="button" className="pr-icon" aria-expanded={isOpen}
            aria-label={isOpen ? 'Hide details' : 'Show details'}
            onClick={() => toggle(row.original.id)}
          >
            {isOpen ? <FaChevronDown /> : <FaChevronRight />}
          </button>
        );
      },
    },
    {
      accessorKey: 'title',
      header: 'Title',
      cell: ({ getValue, row, table }) => (
        <div className="pr-title">
          <EditableCell value={getValue()} onSave={(v) => table.options.meta.update(row.original.id, { title: v })} />
          {row.original.externalLink && (
            <a className="pr-link" href={row.original.externalLink} target="_blank" rel="noreferrer" aria-label="Open problem link">
              <FaExternalLinkAlt />
            </a>
          )}
        </div>
      ),
    },
    {
      accessorKey: 'categoryName',
      header: 'Category',
      cell: ({ getValue, row, table }) => (
        <EditableCell
          value={getValue()} options={CATEGORY_OPTIONS}
          render={(v) => labelOf(CATEGORY_OPTIONS, v)}
          onSave={(v) => table.options.meta.update(row.original.id, { categoryName: v })}
        />
      ),
    },
    {
      accessorKey: 'difficulty',
      header: 'Difficulty',
      sortingFn: rankSort(DIFFICULTY_OPTIONS),
      cell: ({ getValue, row, table }) => (
        <EditableCell
          value={getValue()} options={DIFFICULTY_OPTIONS}
          render={(v) => <Badge prefix="d" value={v} options={DIFFICULTY_OPTIONS} />}
          onSave={(v) => table.options.meta.update(row.original.id, { difficulty: v })}
        />
      ),
    },
    {
      accessorKey: 'status',
      header: 'Status',
      sortingFn: rankSort(STATUS_OPTIONS),
      cell: ({ getValue, row, table }) => (
        <EditableCell
          value={getValue()} options={STATUS_OPTIONS}
          render={(v) => <Badge prefix="s" value={v} options={STATUS_OPTIONS} />}
          onSave={(v) => table.options.meta.update(row.original.id, { status: v })}
        />
      ),
    },
    {
      id: 'tags',
      header: 'Tags',
      accessorFn: (p) => p.tags ?? [],
      enableSorting: false,
      cell: ({ getValue, row, table }) => {
        const tags = getValue();
        return (
          <button type="button" className="pr-tags-btn" title="Click to view and edit tags" onClick={() => table.options.meta.toggle(row.original.id)}>
            {tags.slice(0, 2).map((t) => <span key={t} className="pr-tag">{t}</span>)}
            {tags.length > 2 && <span className="pr-tag more">+{tags.length - 2}</span>}
            {tags.length === 0 && <span className="pr-muted">Add tags</span>}
          </button>
        );
      },
    },
    {
      id: 'actions',
      header: '',
      cell: ({ row, table }) => <DeleteButton onConfirm={() => table.options.meta.remove(row.original.id)} />,
    },
  ], []);

  const table = useReactTable({
    data,
    columns,
    getRowId: (p) => String(p.id),
    state: { sorting, pagination },
    onSortingChange: (updater) => {
      setSorting(updater);
      setPagination((p) => ({ ...p, pageIndex: 0 }));
    },
    onPaginationChange: setPagination,
    autoResetPageIndex: false, // editing a row must not bounce you back to page 1
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    meta: {
      expandedId,
      toggle: (id) => setExpandedId((cur) => (cur === id ? null : id)),
      update: onUpdate,
      remove: onDelete,
    },
  });

  // back to page 1 when filters change; step back if the last page empties out
  useEffect(() => { setPagination((p) => ({ ...p, pageIndex: 0 })); }, [filters]);
  const pageCount = table.getPageCount();
  useEffect(() => {
    if (pageCount > 0 && pagination.pageIndex >= pageCount) {
      setPagination((p) => ({ ...p, pageIndex: pageCount - 1 }));
    }
  }, [pageCount, pagination.pageIndex]);

  const setFilter = (k) => (e) => setFilters((f) => ({ ...f, [k]: e.target.value }));
  const hasFilters = Object.values(filters).some(Boolean);
  const rows = table.getRowModel().rows;
  const { pageIndex, pageSize } = pagination;
  const from = data.length ? pageIndex * pageSize + 1 : 0;
  const to = Math.min((pageIndex + 1) * pageSize, data.length);

  return (
    <div>
      <div className="pr-toolbar">
        <div className="pr-search">
          <FaSearch aria-hidden="true" />
          <input className="pr-field" placeholder="Search by title, category or tag" aria-label="Search problems" value={filters.q} onChange={setFilter('q')} />
        </div>
        <select className="pr-field" aria-label="Filter by status" value={filters.status} onChange={setFilter('status')}>
          <option value="">All statuses</option><Options options={STATUS_OPTIONS} />
        </select>
        <select className="pr-field" aria-label="Filter by category" value={filters.categoryName} onChange={setFilter('categoryName')}>
          <option value="">All categories</option><Options options={CATEGORY_OPTIONS} />
        </select>
        <select className="pr-field" aria-label="Filter by difficulty" value={filters.difficulty} onChange={setFilter('difficulty')}>
          <option value="">All difficulties</option><Options options={DIFFICULTY_OPTIONS} />
        </select>
        {hasFilters && (
          <button type="button" className="pr-btn" onClick={() => setFilters({ q: '', status: '', categoryName: '', difficulty: '' })}>
            Clear filters
          </button>
        )}
      </div>

      <div className="pr-table-wrap">
        <table className="pr-table">
          <thead>
            {table.getHeaderGroups().map((hg) => (
              <tr key={hg.id}>
                {hg.headers.map((header) => {
                  const canSort = header.column.getCanSort();
                  const dir = header.column.getIsSorted();
                  return (
                    <th
                      key={header.id}
                      className={canSort ? 'sortable' : ''}
                      onClick={header.column.getToggleSortingHandler()}
                      aria-sort={dir === 'asc' ? 'ascending' : dir === 'desc' ? 'descending' : undefined}
                    >
                      {flexRender(header.column.columnDef.header, header.getContext())}
                      {dir === 'asc' ? ' ▲' : dir === 'desc' ? ' ▼' : ''}
                    </th>
                  );
                })}
              </tr>
            ))}
          </thead>
          <tbody>
            <QuickAddRow onAdd={onAdd} onCreated={setExpandedId} />
            {rows.map((row) => {
              const open = expandedId === row.original.id;
              return (
                <React.Fragment key={row.id}>
                  <tr className={open ? 'pr-row open' : 'pr-row'}>
                    {row.getVisibleCells().map((cell) => (
                      <td key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</td>
                    ))}
                  </tr>
                  {open && (
                    <tr className="pr-detail-row">
                      <td colSpan={columns.length}>
                        <DetailPanel
                          problem={row.original}
                          onSave={(changes) => onUpdate(row.original.id, changes)}
                          onDeleteProblemTag={(tag) => onDeleteProblemTag(row.original.id, tag)}
                        />
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              );
            })}
            {rows.length === 0 && (
              <tr>
                <td colSpan={columns.length} className="pr-empty">
                  {loading ? 'Loading your problems...' : hasFilters
                    ? 'No problems match these filters. Clear them to see everything.'
                    : 'No problems yet. Type a title in the first row and press Enter to add one.'}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="pr-footer">
        <span>Showing {from} to {to} of {data.length}</span>
        <div className="pr-pager">
          <select className="pr-field" aria-label="Rows per page" value={pageSize} onChange={(e) => table.setPageSize(Number(e.target.value))}>
            {[10, 25, 50].map((n) => <option key={n} value={n}>{n} per page</option>)}
          </select>
          <button type="button" className="pr-btn" onClick={() => table.previousPage()} disabled={!table.getCanPreviousPage()}>Previous</button>
          <span>Page {pageCount ? pageIndex + 1 : 0} of {pageCount}</span>
          <button type="button" className="pr-btn" onClick={() => table.nextPage()} disabled={!table.getCanNextPage()}>Next</button>
        </div>
      </div>
    </div>
  );
};