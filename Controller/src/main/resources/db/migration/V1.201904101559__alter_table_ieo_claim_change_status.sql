ALTER TABLE `IEO_CLAIM` change status status enum ('SUCCESS', 'FAILED', 'NONE', 'REVOKED') default 'NONE' not null;
