
CREATE TABLE studio(
   id BIGINT(20) NOT NULL GENERATED ALWAYS AS IDENTITY,
   reference VARCHAR(40) NOT NULL,
   name VARCHAR(30) NOT NULL,
   payment DECIMAL(10,2) NOT NULL,
   views BIGINT(20) DEFAULT 0,
   PRIMARY KEY(id),
   UNIQUE (reference)
);
CREATE UNIQUE INDEX studio_reference_index ON studio ( reference );

CREATE TABLE episode(
   id BIGINT(20) NOT NULL GENERATED ALWAYS AS IDENTITY,
   reference VARCHAR(40) NOT NULL,
   name VARCHAR(100) NOT NULL,
   rightsowner VARCHAR(40) NOT NULL,
   PRIMARY KEY(id),
   UNIQUE (reference),
);
CREATE UNIQUE INDEX episode_reference_index ON episode ( reference );

ALTER TABLE episode
 ADD CONSTRAINT fk_rightsowner FOREIGN KEY (rightsowner)
   REFERENCES studio(reference)
      ON UPDATE NO ACTION
      ON DELETE CASCADE;