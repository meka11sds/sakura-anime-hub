
-- ==========================================
-- SAKURA ANIME HUB
-- PostgreSQL: SQL Queries and CRUD Examples
-- ==========================================

-- 1. JOIN: пользователи и их списки просмотра
SELECT
    u.username,
    w.title,
    w.status,
    w.current_episode
FROM public.users u
JOIN public.watch_items w
    ON u.id = w.user_id
ORDER BY u.username, w.title;


-- 2. Отчёт: количество аниме у пользователей
SELECT
    u.username,
    COUNT(w.id) AS total_anime,
    COUNT(w.id) FILTER (
        WHERE w.status = 'watching'
    ) AS watching,
    COUNT(w.id) FILTER (
        WHERE w.status = 'completed'
    ) AS completed
FROM public.users u
LEFT JOIN public.watch_items w
    ON u.id = w.user_id
GROUP BY u.id, u.username
ORDER BY total_anime DESC;


-- 3. JOIN: отзывы пользователей об аниме
SELECT
    r.id AS review_id,
    u.username,
    s.title AS anime_title,
    r.rating,
    r.comment
FROM public.reviews r
JOIN public.users u
    ON r.user_id = u.id
JOIN public.shows s
    ON r.show_id = s.id
ORDER BY r.id DESC;


-- 4. Отчёт: средние оценки аниме
SELECT
    s.title AS anime_title,
    COUNT(r.id) AS total_reviews,
    ROUND(AVG(r.rating), 2) AS average_rating,
    MIN(r.rating) AS lowest_rating,
    MAX(r.rating) AS highest_rating
FROM public.shows s
LEFT JOIN public.reviews r
    ON s.id = r.show_id
GROUP BY s.id, s.title
ORDER BY total_reviews DESC, s.title;


-- ==========================================
-- 5. CRUD: WATCH_ITEMS
-- Демонстрация на тестовой записи.
-- ROLLBACK отменяет изменения.
-- ==========================================

BEGIN;

-- CREATE
-- Используем существующего пользователя и аниме.
INSERT INTO public.watch_items (
    user_id,
    show_id,
    title,
    status,
    current_episode
)
SELECT
    u.id,
    s.id,
    s.title,
    'planned',
    1
FROM public.users u
CROSS JOIN public.shows s
ORDER BY u.id, s.id
LIMIT 1
RETURNING id, title, status;

-- READ
SELECT
    w.id,
    u.username,
    w.title,
    w.status,
    w.current_episode
FROM public.watch_items w
JOIN public.users u ON w.user_id = u.id
ORDER BY w.id DESC;

-- UPDATE
-- Обновляем только созданную в этой транзакции запись.
UPDATE public.watch_items
SET
    status = 'watching',
    current_episode = 3
WHERE id = currval(
    pg_get_serial_sequence('public.watch_items', 'id')
)
RETURNING id, title, status, current_episode;

-- DELETE
DELETE FROM public.watch_items
WHERE id = currval(
    pg_get_serial_sequence('public.watch_items', 'id')
)
RETURNING id, title;

ROLLBACK;


-- ==========================================
-- 6. CRUD: REVIEWS
-- Демонстрация на тестовом отзыве.
-- ROLLBACK отменяет изменения.
-- ==========================================

BEGIN;

-- CREATE
INSERT INTO public.reviews (
    user_id,
    show_id,
    rating,
    comment
)
SELECT
    u.id,
    s.id,
    8,
    'Test review for coursework'
FROM public.users u
CROSS JOIN public.shows s
ORDER BY u.id, s.id
LIMIT 1
RETURNING id, rating, comment;

-- READ
SELECT
    r.id,
    u.username,
    s.title,
    r.rating,
    r.comment
FROM public.reviews r
JOIN public.users u ON r.user_id = u.id
JOIN public.shows s ON r.show_id = s.id
ORDER BY r.id DESC;

-- UPDATE
UPDATE public.reviews
SET
    rating = 10,
    comment = 'Updated test review'
WHERE id = currval(
    pg_get_serial_sequence('public.reviews', 'id')
)
RETURNING id, rating, comment;

-- DELETE
DELETE FROM public.reviews
WHERE id = currval(
    pg_get_serial_sequence('public.reviews', 'id')
)
RETURNING id;

ROLLBACK;
