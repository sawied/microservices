CREATE DATABASE crawl;

CREATE USER 'crawler' IDENTIFIED BY 'crawler';
GRANT ALL PRIVILEGES ON crawl.* TO 'crawler' WITH GRANT OPTION;
FLUSH PRIVILEGES;

# DROP TABLE crawl.urls;

CREATE TABLE crawl.urls (
 url VARCHAR(255),
 status VARCHAR(16) DEFAULT 'DISCOVERED',
 nextfetchdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 metadata TEXT,
 bucket SMALLINT DEFAULT 0,
 host VARCHAR(128),
 PRIMARY KEY(url)
);

alter table crawl.urls 
add column correlationId varchar(50) not null;

ALTER TABLE crawl.urls ADD INDEX b (`bucket`);
ALTER TABLE crawl.urls ADD INDEX t (`nextfetchdate`);
ALTER TABLE crawl.urls ADD INDEX h (`host`);
ALTER TABLE crawl.urls ADD INDEX c (`correlationId`);

# DROP TABLE crawl.metrics;

CREATE TABLE crawl.metrics (
 srcComponentId VARCHAR(128),
 srcTaskId INT,
 srcWorkerHost VARCHAR(128),
 srcWorkerPort INT,
 name VARCHAR(128),
 value DOUBLE,
 timestamp TIMESTAMP
);

# Read only user for accessing the metrics
CREATE USER 'metricsReader' IDENTIFIED BY 'metricsReader';
GRANT SELECT ON crawl.metrics TO 'metricsReader';
FLUSH PRIVILEGES;


#INSERT INTO `crawl`.`urls` (`url`,`correlationid`) VALUES ('https://www.sawied.top','109fb598-bb17-4ef3-bd36-52354cac02d3');
#commit;