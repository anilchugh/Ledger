insert into entity(name) values('ENTITY_NAME');

insert into account(name, status, entity_id) values('ACCOUNT_NAME', 'OPEN', select(id) from entity where name = 'ENTITY_NAME');

insert into wallet(asset_type, balance, account_id) values('FIAT_CURRENCY', '190', select(id) from account where name = 'ACCOUNT_NAME');
insert into wallet(asset_type, balance, account_id) values('FIAT_CURRENCY', '310', select(id) from account where name = 'ACCOUNT_NAME');

insert into posting(account_id, amount, source_wallet_id, destination_wallet_id, status, last_modified) values(select(id) from account where name = 'ACCOUNT_NAME', 10, 1, 2, 'CLEARED', CURRENT_TIMESTAMP);
