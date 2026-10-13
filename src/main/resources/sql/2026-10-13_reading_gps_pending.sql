-- Apply before backend deployment. Does not invent GPS for existing captures.
ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS reading_trace_status varchar(32);
