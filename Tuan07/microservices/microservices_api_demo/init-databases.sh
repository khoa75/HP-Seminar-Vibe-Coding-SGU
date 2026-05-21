<<<<<<< HEAD
<<<<<<< HEAD
# Tạo các database khi container bắt đầu
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE order_db;
    CREATE DATABASE inventory_db;
EOSQL
=======
=======
>>>>>>> dev
# Tạo các database khi container bắt đầu
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE order_db;
    CREATE DATABASE inventory_db;
EOSQL
<<<<<<< HEAD
>>>>>>> 5097b7c3 (Tuan07/Init commit for Tuan07)
=======
>>>>>>> dev
