INSERT INTO contact_types(type_id, type_name)
VALUES      ('85d0baa8-736d-43be-b1aa-c29f06cfeb24', 'TEST CONTACT TYPE ONE');

INSERT INTO training_partners(partner_id, partner_name)
VALUES      ('5c89a467-ae77-42e2-82db-076039f9b386', 'TEST TRAINING PARTNER ONE');

INSERT INTO partner_contacts(contact_id, first_name, last_name, primary_phone, mobile_phone, email_address, partner_id, type_id)
VALUES      ('422daaba-be43-4405-8a85-521b5ecfb26a', 'FIRST', 'NAME', '1234567890', '9876543210', 'usera@company.com', '5c89a467-ae77-42e2-82db-076039f9b386', '85d0baa8-736d-43be-b1aa-c29f06cfeb24');
