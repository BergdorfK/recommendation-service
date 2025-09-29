--liquibase formatted sql

--changeset you:001-create-dynamic-rule
CREATE TABLE IF NOT EXISTS public.dynamic_rule (
    id           uuid PRIMARY KEY,
    product_id   uuid NOT NULL UNIQUE,
    product_code varchar NULL,
    product_name text  NOT NULL,
    product_text text  NOT NULL,
    rule_json    jsonb NOT NULL,
    created_at   timestamptz NULL
);
--rollback DROP TABLE IF EXISTS public.dynamic_rule;

--changeset you:002-create-dynamic-rule-stats
CREATE TABLE IF NOT EXISTS public.dynamic_rule_stats (
     rule_id uuid PRIMARY KEY,
     count   bigint NOT NULL DEFAULT 0
);
--rollback DROP TABLE IF EXISTS public.dynamic_rule_stats;

--changeset you:002b-fk-rule-stats-rule splitStatements:false endDelimiter:$$
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
      FROM pg_constraint
     WHERE conname = 'fk_rule_stats_rule'
       AND conrelid = 'public.dynamic_rule_stats'::regclass
  ) THEN
ALTER TABLE public.dynamic_rule_stats
    ADD CONSTRAINT fk_rule_stats_rule
        FOREIGN KEY (rule_id) REFERENCES public.dynamic_rule(id)
            ON DELETE CASCADE;
END IF;
END
$$;
--rollback ALTER TABLE public.dynamic_rule_stats DROP CONSTRAINT IF EXISTS fk_rule_stats_rule;