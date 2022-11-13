BEGIN TRANSACTION;

INSERT INTO CURRENCY (code, name, type)
VALUES
    ('USD', 'United States Dollar', 'FIAT'),
    ('EUR', 'Euro', 'FIAT'),
    ('BTC', 'Bitcoin', 'CRYPTO'),
    ('ETH', 'Ethereum', 'CRYPTO'),
    ('USDT', 'Tether', 'CRYPTO');

INSERT INTO CURRENCY_EXCHANGE (from_id, to_id)
SELECT src.id, tgt.id
FROM (
         SELECT 'BTC'  AS source, 'USD' AS target UNION ALL
         SELECT 'ETH'  AS source, 'USD' AS target UNION ALL
         SELECT 'USDT' AS source, 'USD' AS target UNION ALL
         SELECT 'BTC'  AS source, 'EUR' AS target UNION ALL
         SELECT 'ETH'  AS source, 'EUR' AS target UNION ALL
         SELECT 'USDT' AS source, 'EUR' AS target UNION ALL
         SELECT 'USD'  AS source, 'EUR' AS target UNION ALL
         SELECT 'EUR'  AS source, 'USD' AS target
     ) AS t
     JOIN CURRENCY src ON src.code = t.source
     JOIN CURRENCY tgt ON tgt.code = t.target;

COMMIT;