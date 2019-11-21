/**
 *  Engine: MySQL
 *  Version: 0.1
 */

/*****************************************************************/
/* APP                                                           */
/*****************************************************************/
CREATE TABLE app (
  id   INT(11)     NOT NULL,
  description VARCHAR(256) NOT NULL,
  token VARCHAR(50) NOT NULL,
  from_address VARCHAR(50) NOT NULL, -- Withdraw address to send SKINs to users
  from_password VARCHAR(256) NOT NULL,
  callback_url VARCHAR(256) NULL,
  enable_callback TINYINT(1) DEFAULT '0',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*****************************************************************/
/* NOTIFICATION                                                  */
/*****************************************************************/
CREATE TABLE notification (
  id VARCHAR(256) NOT NULL,
  app_id int(11) NOT NULL,
  gateway VARCHAR(10) NOT NULL,
  type int(1) NULL,
  creating_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  request_count int(11) NOT NULL DEFAULT '0',
  data VARCHAR(4000),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_notification_app ON notification (app_id);
CREATE INDEX idx_notification_time ON notification (creating_time);

/*****************************************************************/
/* FILTER                                                        */
/*****************************************************************/
CREATE TABLE eth_filter (
  name VARCHAR(255) NOT NULL,
  value DECIMAL(65,0) NOT NULL,
  PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*****************************************************************/
/* ACCOUNT                                                       */
/*****************************************************************/
CREATE TABLE eth_account (
  app_id int(11) NOT NULL,
  account_id VARCHAR(50) NOT NULL,
  request_id VARCHAR(256) NULL,
  creating_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  address VARCHAR(50) NOT NULL,
  password VARCHAR(50) NOT NULL,
  status int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (app_id,account_id) -- Status for partition
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_eth_account_request ON eth_account (request_id);
CREATE INDEX idx_eth_account_status ON eth_account (status);


/*****************************************************************/
/* PAYMENT                                                   */
/*****************************************************************/
CREATE TABLE eth_payment (
  app_id int(11) NOT NULL,
  type int(1) NOT NULL,
  request_id VARCHAR(256) NOT NULL,
  hash VARCHAR(100) NULL,
  creating_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  from_address VARCHAR(50) NULL,
  to_address VARCHAR(50) NOT NULL,
  currency varchar(5)  NOT NULL,
  status int(11) NOT NULL,
  amount DECIMAL(65,0) NOT NULL,
  error VARCHAR(4000) NULL,
  stack_trace VARCHAR(4000) NULL,
  PRIMARY KEY (app_id, type, request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*****************************************************************/
/* TRANSACTION                                                   */
/*****************************************************************/
CREATE TABLE eth_transaction (
  hash VARCHAR(100) NOT NULL,
  block_hash VARCHAR(100) NULL,
  block_number DECIMAL(65,0) NULL,
  nonce int(11) NULL,
  creating_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hold_time timestamp NULL,
  from_address VARCHAR(50) NULL,
  to_address VARCHAR(50) NOT NULL,
  status int(11) NOT NULL,
  amount DECIMAL(65,0) NOT NULL,
  currency varchar(5)  NOT NULL,
  gas DECIMAL(65,0) NULL,
  gas_used DECIMAL(65,0) NULL,
  cumulative_gas_used DECIMAL(65,0) NULL,
  gas_price DECIMAL(65,0) NULL,
  locked tinyint(1) NOT NULL DEFAULT '0',
  error VARCHAR(4000) NULL,
  stack_trace VARCHAR(4000) NULL,
  PRIMARY KEY (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_eth_txn_hash ON eth_transaction (hash);
CREATE INDEX idx_eth_txn_from ON eth_transaction (from_address);
CREATE INDEX idx_eth_txn_to ON eth_transaction (to_address);
CREATE INDEX idx_eth_txn_status ON eth_transaction (status);
