curl -X PUT http://localhost:9200/_template/mongoes -H 'Content-Type: application/json' -d @mongo_template.json

curl -X POST http://localhost:9200/mongodb_es/_mapping/ossinfo -H 'Content-Type: application/json' -d @fields.json