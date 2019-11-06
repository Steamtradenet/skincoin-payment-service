/**
 *  Engine: MySQL
 *  Version: 0.2
 */

/*****************************************************************/
/* APP                                                           */
/*****************************************************************/
ALTER TABLE app ADD auth_secret VARCHAR(255) NULL;

/*****************************************************************/
/* PAYMENT                                                       */
/*****************************************************************/
ALTER TABLE eth_payment ADD payment_request_id VARCHAR(255) NULL;

/*****************************************************************/
/* PENDING TRANSACTIONS                                          */
/*****************************************************************/
CREATE TABLE eth_transaction_pending (
  hash VARCHAR(100) NOT NULL,
  locked tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;