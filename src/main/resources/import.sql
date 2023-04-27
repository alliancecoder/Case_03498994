INSERT INTO contact_types(type_id, type_name)
VALUES      ('85d0baa8-736d-43be-b1aa-c29f06cfeb24', 'TEST CONTACT TYPE ONE');

INSERT INTO training_partners(partner_id, partner_name)
VALUES      ('5c89a467-ae77-42e2-82db-076039f9b386', 'TEST TRAINING PARTNER ONE');

INSERT INTO partner_contacts(contact_id, first_name, last_name, primary_phone, mobile_phone, email_address, partner_id, type_id)
VALUES      ('422daaba-be43-4405-8a85-521b5ecfb26a', 'FIRST', 'NAME', '1234567890', '9876543210', 'usera@company.com', '5c89a467-ae77-42e2-82db-076039f9b386', '85d0baa8-736d-43be-b1aa-c29f06cfeb24');

INSERT INTO locations(location_id, building_name, room)
VALUES      ('bf66fdb5-5241-4a82-b77b-a3aa68ed5b54', 'TEST BUILDING ONE', 'R-001');

INSERT INTO session_types(type_id, type_name)
VALUES      ('52c21089-0211-4772-8fc6-3f9574054418', 'TEST SESSION TYPE ONE');

INSERT INTO session_content(content_id, content_name, content_description, type_id)
VALUES      ('4413ed82-78a3-45cb-a76d-42597dd0f5ae', 'TEST CONTENT ONE', 'CONTENT ONE DESCRIPTION', '52c21089-0211-4772-8fc6-3f9574054418');

INSERT INTO time_slots(slot_id, slot_day, slot_date, slot_time)
VALUES      ('8f25a584-510f-41a2-9d6a-f14ed06187b7', 'THURSDAY', '1973-09-06', '9:00:00 PM');

