-- Drop indexes if they exist
DROP INDEX IF EXISTS index_users_login;
DROP INDEX IF EXISTS index_rentalOrder_login;

DROP INDEX IF EXISTS index_catalog_gameID;
DROP INDEX IF EXISTS index_gamesInOrder_gameID;

DROP INDEX IF EXISTS index_rentalOrder_rentalOrderID;
DROP INDEX IF EXISTS index_trackingInfo_rentalOrderID;
DROP INDEX IF EXISTS index_gamesInOrder_rentalOrderID;

DROP INDEX IF EXISTS index_trackingInfo_trackingID;

-- Create indexes
CREATE INDEX IF NOT EXISTS index_users_login ON Users(login);
CREATE INDEX IF NOT EXISTS index_rentalOrder_login ON RentalOrder(login);

CREATE INDEX IF NOT EXISTS index_catalog_gameID ON Catalog(gameID);
CREATE INDEX IF NOT EXISTS index_gamesInOrder_gameID ON GamesInOrder (gameID);

CREATE INDEX IF NOT EXISTS index_rentalOrder_rentalOrderID ON RentalOrder(rentalOrderID);
CREATE INDEX IF NOT EXISTS index_trackingInfo_rentalOrderID ON TrackingInfo(rentalOrderID);
CREATE INDEX IF NOT EXISTS index_gamesInOrder_rentalOrderID ON GamesInOrder(rentalOrderID);

CREATE INDEX IF NOT EXISTS index_trackingInfo_trackingID ON TrackingInfo(trackingID);