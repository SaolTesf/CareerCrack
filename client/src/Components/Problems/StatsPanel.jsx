import React, { useMemo } from 'react';
import { CATEGORIES } from '../../services/problemCategoryService';

const DONE = ['SOLVED', 'MASTERED'];
const STATUSES = [
  ['TODO', 'To do'], ['ATTEMPTED', 'Attempted'], ['REVIEW', 'Review'],
  ['SOLVED', 'Solved'], ['MASTERED', 'Mastered'],
];
const DIFFICULTIES = ['easy', 'medium', 'hard', 'hardcore'];
const pct = (n, d) => (d ? Math.round((n / d) * 100) : 0);
const cap = (s) => s[0].toUpperCase() + s.slice(1);

// { key: { total, done } }
const group = (problems, key) =>
  problems.reduce((acc, p) => {
    const k = p[key] ?? 'unknown';
    acc[k] ??= { total: 0, done: 0 };
    acc[k].total += 1;
    if (DONE.includes(p.status)) acc[k].done += 1;
    return acc;
  }, {});

const Bar = ({ label, done, total }) => (
  <div className="pr-bar">
    <div className="pr-bar-label"><span>{label}</span><span>{done} of {total} done</span></div>
    <div className="pr-bar-track"><div className="pr-bar-fill" style={{ width: `${pct(done, total)}%` }} /></div>
  </div>
);

export const StatsPanel = ({ problems }) => {
  const s = useMemo(() => {
    const byStatus = {};
    const tagCounts = {};
    problems.forEach((p) => {
      byStatus[p.status] = (byStatus[p.status] ?? 0) + 1;
      (p.tags ?? []).forEach((t) => { tagCounts[t] = (tagCounts[t] ?? 0) + 1; });
    });
    const weekAgo = Date.now() - 7 * 864e5;
    return {
      total: problems.length,
      done: problems.filter((p) => DONE.includes(p.status)).length,
      thisWeek: problems.filter((p) => new Date(p.updatedAt) > weekAgo).length,
      byStatus,
      byDifficulty: group(problems, 'difficulty'),
      byCategory: group(problems, 'categoryName'),
      topTags: Object.entries(tagCounts).sort((a, b) => b[1] - a[1]).slice(0, 12),
    };
  }, [problems]);

  const noData = <p className="pr-muted">Add a problem to see this fill in.</p>;

  return (
    <section className="pr-overview" aria-label="Progress overview">
      <div className="pr-col">
        <div className="pr-headline">
          <strong>{pct(s.done, s.total)}%</strong>
          <span>{s.done} of {s.total} problems solved or mastered</span>
        </div>
        <div className="pr-segments" role="img" aria-label="Problems by status">
          {STATUSES.map(([k, label]) => s.byStatus[k] && (
            <span key={k} className={`seg s-${k.toLowerCase()}`} style={{ flexGrow: s.byStatus[k] }} title={`${label}: ${s.byStatus[k]}`} />
          ))}
        </div>
        <ul className="pr-legend">
          {STATUSES.map(([k, label]) => (
            <li key={k}><i className={`dot s-${k.toLowerCase()}`} />{label} <b>{s.byStatus[k] ?? 0}</b></li>
          ))}
        </ul>
        <p className="pr-muted">{s.thisWeek} updated in the last 7 days</p>
      </div>

      <div className="pr-col">
        <h3>By difficulty</h3>
        {s.total === 0 ? noData : DIFFICULTIES.filter((d) => s.byDifficulty[d]).map((d) => (
          <Bar key={d} label={cap(d)} {...s.byDifficulty[d]} />
        ))}
      </div>

      <div className="pr-col">
        <h3>By category</h3>
        {s.total === 0 ? noData : CATEGORIES.filter((c) => s.byCategory[c.name]).map((c) => (
          <Bar key={c.name} label={c.label} {...s.byCategory[c.name]} />
        ))}
      </div>

      <div className="pr-col">
        <h3>Most used tags</h3>
        {s.topTags.length === 0 ? <p className="pr-muted">Tag a problem to see your top topics.</p> : (
          <div className="pr-tag-list">
            {s.topTags.map(([t, n]) => <span key={t} className="pr-tag">{t}<b>{n}</b></span>)}
          </div>
        )}
      </div>
    </section>
  );
};