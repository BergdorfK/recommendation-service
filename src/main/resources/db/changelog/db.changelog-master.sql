-- changeset you:001-create-dynamic-rule
CREATE TABLE IF NOT EXISTS public.dynamic_rule (
                                                   id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id   UUID NOT NULL UNIQUE,
    product_code VARCHAR(64),
    product_name VARCHAR(255) NOT NULL,
    product_text TEXT NOT NULL,
    rule_json    JSONB NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT now()
    );

-- changeset you:002-create-dynamic-rule-stats
CREATE TABLE IF NOT EXISTS public.dynamic_rule_stats (
                                                         rule_id UUID PRIMARY KEY,
                                                         count   BIGINT NOT NULL DEFAULT 0
);

-- changeset you:002b-fk-rule-stats-rule splitStatements:false endDelimiter:$$
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
-- rollback ALTER TABLE public.dynamic_rule_stats DROP CONSTRAINT IF EXISTS fk_rule_stats_rule;