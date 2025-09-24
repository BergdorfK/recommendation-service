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
