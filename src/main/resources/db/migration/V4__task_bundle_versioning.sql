alter table task_bundle add column version int default 0;
alter table task_bundle add column created_at timestamp;
alter table task_bundle add column last_updated timestamp;
alter table task_bundle add column last_task_update timestamp;