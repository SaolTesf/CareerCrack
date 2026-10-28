import React, { useCallback, useEffect, useRef, useState } from 'react';
import { problemService } from '../../services/problemService';
import { tagService } from '../../services/tagService';
import { authService } from '../../services/authService';
import { CATEGORIES } from '../../services/problemCategoryService';
import { StatsPanel } from './StatsPanel';
import { ProblemsTable } from './ProblemsTable';
import './Problems.css';

// Loads everything in one request so stats, search and filters work across all problems.
// Fine for a personal tracker; if it grows past a few thousand rows, add a stats endpoint.
const PAGE_SIZE = 1000;

const errorText = (e) =>
  e?.fieldErrors ? Object.values(e.fieldErrors).join(', ') : e?.message || 'Something went wrong';

// PUT replaces the whole record, so always send every field.
const toUpdateBody = (p) => ({
  category: p.categoryName,
  title: p.title,
  externalLink: p.externalLink,
  difficulty: p.difficulty,
  status: p.status,
  description: p.description,
  solution: p.solution,
  tags: p.tags ?? [],
});

const Problems = () => {
  const [problems, setProblems] = useState([]);
  const [allTags, setAllTags] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);
  const timer = useRef();

  const notify = useCallback((text, type = 'error') => {
    clearTimeout(timer.current);
    setToast({ text, type });
    timer.current = setTimeout(() => setToast(null), 4000);
  }, []);

  useEffect(() => {
    Promise.all([
      problemService.getMine({ page: 0, size: PAGE_SIZE, sortBy: 'id', ascending: false }),
      tagService.getAll(),
    ])
      .then(([page, tags]) => {
        setProblems(page.content);
        setAllTags(tags.map((t) => t.name));
      })
      .catch((e) => notify(errorText(e)))
      .finally(() => setLoading(false));
  }, [notify]);

  const addProblem = async (draft) => {
    const user = authService.getCurrentUser();
    const category = CATEGORIES.find((c) => c.name === draft.categoryName);
    try {
      const created = await problemService.create({
        userId: user.id,
        categoryId: category.id,
        title: draft.title,
        difficulty: draft.difficulty,
        status: draft.status,
        tags: [],
      });
      setProblems((prev) => [created, ...prev]);
      return created;
    } catch (e) {
      notify(errorText(e));
      return null;
    }
  };

  // Optimistic: update the UI first, roll back if the server rejects it.
  const updateProblem = async (id, changes) => {
    const before = problems.find((p) => p.id === id);
    if (!before) return;
    const next = { ...before, ...changes };
    setProblems((prev) => prev.map((p) => (p.id === id ? next : p)));
    try {
      const saved = await problemService.update(id, toUpdateBody(next));
      setProblems((prev) => prev.map((p) => (p.id === id ? saved : p)));
      setAllTags((prev) => [...new Set([...prev, ...(saved.tags ?? [])])]);
    } catch (e) {
      setProblems((prev) => prev.map((p) => (p.id === id ? before : p)));
      notify(errorText(e));
    }
  };

  const deleteProblem = async (id) => {
    const before = problems;
    setProblems((prev) => prev.filter((p) => p.id !== id));
    try {
      await problemService.remove(id);
      notify('Problem deleted', 'success');
    } catch (e) {
      setProblems(before);
      notify(errorText(e));
    }
  };

  const deleteProblemTag = async (problemId, tagName) => {
    const before = problems.find((p) => p.id === problemId);
    if (!before) return;
    setProblems((prev) => prev.map((p) =>
      p.id === problemId ? { ...p, tags: (p.tags ?? []).filter((t) => t !== tagName) } : p));
    try {
      const saved = await problemService.deleteProblemTag(problemId, tagName);
      setProblems((prev) => prev.map((p) => (p.id === problemId ? saved : p)));
    } catch (e) {
      setProblems((prev) => prev.map((p) => (p.id === problemId ? before : p)));
      notify(errorText(e));
    }
  };

  return (
    <div className="pr-page">
      <header className="pr-header">
        <h1>My problems</h1>
        <p>Track what you have solved, what needs review, and what is still to do.</p>
      </header>

      <StatsPanel problems={problems} />

      <ProblemsTable
        problems={problems}
        loading={loading}
        onAdd={addProblem}
        onUpdate={updateProblem}
        onDelete={deleteProblem}
        onDeleteProblemTag={deleteProblemTag}
      />

      <datalist id="pr-tag-options">
        {allTags.map((t) => <option key={t} value={t} />)}
      </datalist>

      {toast && <div className={`pr-toast ${toast.type}`} role="status">{toast.text}</div>}
    </div>
  );
};

export default Problems;