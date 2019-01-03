-- new version
INSERT INTO public.application_users (id, username, email, password) VALUES (nextval('hibernate_sequence'), 'ben', 'john@email.com', '$2a$10$L4GdGfFSeovJdicxMJSrOuHRS7JXbo4ceB5/c1K8NOb8bMtNzsIa6'), (nextval('hibernate_sequence'), 'Jane', 'jane@email.com', '$2a$10$L4GdGfFSeovJdicxMJSrOuHRS7JXbo4ceB5/c1K8NOb8bMtNzsIa6');

-- dumps for testing
INSERT INTO public.task_subscription (id, name, necessary_completions, period, weight, active) VALUES (3, 'task1', 1, 1, 1, TRUE);
INSERT INTO public.task_subscription (id, name, necessary_completions, period, weight, active) VALUES (5, 'task2', 1, 2, 5, TRUE);
INSERT INTO public.task_instance (id, completions, due_at) VALUES (4, 0, '2018-12-15');
INSERT INTO public.task_instance (id, completions, due_at) VALUES (6, 0, '2018-12-21');
INSERT INTO public.task_subscription_task_instance VALUES (3,4);
INSERT INTO public.task_subscription_task_instance VALUES (5,6);
INSERT INTO public.application_users_task_subscription (user_entity_id, task_subscriptions_id) VALUES (1, 3);
INSERT INTO public.application_users_task_subscription (user_entity_id, task_subscriptions_id) VALUES (1, 5);

INSERT INTO public.user_roles (id, role) VALUES (7, 'ADMIN');
INSERT INTO public.user_roles (id, role) VALUES (8, 'ADMIN');
INSERT INTO public.application_users_user_roles VALUES (1,7);
INSERT INTO public.application_users_user_roles VALUES (2,8);
select setval('hibernate_sequence', 8);