-- INSERT INTO user (firstName, lastName, email, passwordHash) VALUES ("John", "Davis", "john@email.com", "XXXXX");

-- INSERT INTO task_entity (task_id, name, period, user_id, weight, minimum_completions) VALUES (nextval('hibernate_sequence'), 'Mop Floors', 7, 1, 5, 1), (nextval('hibernate_sequence'), 'Buy groceries', 7, 2, 3, 1), (nextval('hibernate_sequence'), 'Play guitar', 1, 1, 2, 2), (nextval('hibernate_sequence'), 'Code', 1, 2, 2, 1), (nextval('hibernate_sequence'), 'Do Laundry', 7, 1, 2, 1), (nextval('hibernate_sequence'), 'Read', 1, 2, 2, 1);

-- INSERT INTO user_tasks (id, task_id, user_id, due_at) VALUES (nextval('hibernate_sequence'), 1, 1, '2018-05-05 18:00:00'), (nextval('hibernate_sequence'), 2, 2, '2018-08-08 18:00:00');

-- new version
INSERT INTO application_users (id, first_name, last_name, email, password_hash) VALUES (nextval('hibernate_sequence'), 'John', 'Davis', 'john@email.com', 'XXXX'), (nextval('hibernate_sequence'), 'Jane', 'Doe', 'jane@email.com', 'YYYY');


-- dumps for testing
INSERT INTO public.task_subscription (id, name, necessary_completions, period, weight, active) VALUES (3, 'task1', 1, 1, 1, TRUE);
INSERT INTO public.task_subscription (id, name, necessary_completions, period, weight, active) VALUES (5, 'task2', 1, 2, 5, TRUE);

INSERT INTO public.task_instance (id, completions, due_at) VALUES (4, 0, '2018-12-15');
INSERT INTO public.task_instance (id, completions, due_at) VALUES (6, 0, '2018-12-21');

INSERT INTO public.task_subscription_task_instance VALUES (3,4);
INSERT INTO public.task_subscription_task_instance VALUES (5,6);

INSERT INTO public.application_users_task_subscription (user_entity_id, task_subscriptions_id) VALUES (1, 3);
INSERT INTO public.application_users_task_subscription (user_entity_id, task_subscriptions_id) VALUES (1, 5);

-- ALTER SEQUENCE hibernate_sequence INCREMENT 5;

select setval('hibernate_sequence', 6);