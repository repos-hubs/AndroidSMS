/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kindroid.kincent.data;

import com.kindroid.google.android.mms.pdu.PduHeaders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import com.kindroid.android.provider.Telephony.Mms;
import com.kindroid.android.provider.Telephony.MmsSms;
import com.kindroid.android.provider.Telephony.Mms.Addr;
import com.kindroid.android.provider.Telephony.Mms.Part;
import com.kindroid.android.provider.Telephony.Mms.Rate;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The class to provide base facility to access MMS related content,
 * which is stored in a SQLite database and in the file system.
 */
public class MmsProvider extends ContentProvider {
    static final String TABLE_PDU  = "pdu";
    static final String TABLE_ADDR = "addr";
    static final String TABLE_PART = "part";
    static final String TABLE_RATE = "rate";
    static final String TABLE_DRM  = "drm";

    @Override
    public boolean onCreate() {
        mOpenHelper = MmsSmsDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query.
        int match = sURLMatcher.match(uri);
        if (LOCAL_LOGV) {
            Log.v(TAG, "Query uri=" + uri + ", match=" + match);
        }

        switch (match) {
            case MMS_ALL:
                constructQueryForBox(qb, Mms.MESSAGE_BOX_ALL);
                break;
            case MMS_INBOX:
                constructQueryForBox(qb, Mms.MESSAGE_BOX_INBOX);
                break;
            case MMS_SENT:
                constructQueryForBox(qb, Mms.MESSAGE_BOX_SENT);
                break;
            case MMS_DRAFTS:
                constructQueryForBox(qb, Mms.MESSAGE_BOX_DRAFTS);
                break;
            case MMS_OUTBOX:
                constructQueryForBox(qb, Mms.MESSAGE_BOX_OUTBOX);
                break;
            case MMS_ALL_ID:
                qb.setTables(TABLE_PDU);
                qb.appendWhere(Mms._ID + "=" + uri.getPathSegments().get(0));
                break;
            case MMS_INBOX_ID:
            case MMS_SENT_ID:
            case MMS_DRAFTS_ID:
            case MMS_OUTBOX_ID:
                qb.setTables(TABLE_PDU);
                qb.appendWhere(Mms._ID + "=" + uri.getPathSegments().get(1));
                qb.appendWhere(" AND " + Mms.MESSAGE_BOX + "="
                        + getMessageBoxByMatch(match));
                break;
            case MMS_ALL_PART:
                qb.setTables(TABLE_PART);
                break;
            case MMS_MSG_PART:
                qb.setTables(TABLE_PART);
                qb.appendWhere(Part.MSG_ID + "=" + uri.getPathSegments().get(0));
                break;
            case MMS_PART_ID:
                qb.setTables(TABLE_PART);
                qb.appendWhere(Part._ID + "=" + uri.getPathSegments().get(1));
                break;
            case MMS_MSG_ADDR:
                qb.setTables(TABLE_ADDR);
                qb.appendWhere(Addr.MSG_ID + "=" + uri.getPathSegments().get(0));
                break;
            case MMS_REPORT_STATUS:
                /*
                   SELECT DISTINCT address,
                                   T.delivery_status AS delivery_status,
                                   T.read_status AS read_status
                   FROM addr
                   INNER JOIN (SELECT P1._id AS id1, P2._id AS id2, P3._id AS id3,
                                      ifnull(P2.st, 0) AS delivery_status,
                                      ifnull(P3.read_status, 0) AS read_status
                               FROM pdu P1
                               INNER JOIN pdu P2
                               ON P1.m_id = P2.m_id AND P2.m_type = 134
                               LEFT JOIN pdu P3
                               ON P1.m_id = P3.m_id AND P3.m_type = 136
                               UNION
                               SELECT P1._id AS id1, P2._id AS id2, P3._id AS id3,
                                      ifnull(P2.st, 0) AS delivery_status,
                                      ifnull(P3.read_status, 0) AS read_status
                               FROM pdu P1
                               INNER JOIN pdu P3
                               ON P1.m_id = P3.m_id AND P3.m_type = 136
                               LEFT JOIN pdu P2
                               ON P1.m_id = P2.m_id AND P2.m_type = 134) T
                   ON (msg_id = id2 AND type = 151)
                   OR (msg_id = id3 AND type = 137)
                   WHERE T.id1 = ?;
                 */
                qb.setTables("addr INNER JOIN (SELECT P1._id AS id1, P2._id" +
                             " AS id2, P3._id AS id3, ifnull(P2.st, 0) AS" +
                             " delivery_status, ifnull(P3.read_status, 0) AS" +
                             " read_status FROM pdu P1 INNER JOIN pdu P2 ON" +
                             " P1.m_id=P2.m_id AND P2.m_type=134 LEFT JOIN" +
                             " pdu P3 ON P1.m_id=P3.m_id AND P3.m_type=136" +
                             " UNION SELECT P1._id AS id1, P2._id AS id2, P3._id" +
                             " AS id3, ifnull(P2.st, 0) AS delivery_status," +
                             " ifnull(P3.read_status, 0) AS read_status FROM" +
                             " pdu P1 INNER JOIN pdu P3 ON P1.m_id=P3.m_id AND" +
                             " P3.m_type=136 LEFT JOIN pdu P2 ON P1.m_id=P2.m_id" +
                             " AND P2.m_type=134) T ON (msg_id=id2 AND type=151)" +
                             " OR (msg_id=id3 AND type=137)");
                qb.appendWhere("T.id1 = " + uri.getLastPathSegment());
                qb.setDistinct(true);
                break;
            case MMS_REPORT_REQUEST:
                /*
                   SELECT address, d_rpt, rr
                   FROM addr join pdu on pdu._id = addr.msg_id
                   WHERE pdu._id = messageId AND addr.type = 151
                 */
                qb.setTables(TABLE_ADDR + " join " +
                        TABLE_PDU + " on pdu._id = addr.msg_id");
                qb.appendWhere("pdu._id = " + uri.getLastPathSegment());
                qb.appendWhere(" AND " + "addr.type = " + PduHeaders.TO);
                break;
            case MMS_SENDING_RATE:
                qb.setTables(TABLE_RATE);
                break;
            case MMS_DRM_STORAGE_ID:
                qb.setTables(TABLE_DRM);
                qb.appendWhere(BaseColumns._ID + "=" + uri.getLastPathSegment());
                break;
            case MMS_THREADS:
                qb.setTables("pdu group by thread_id");
                break;
            default:
                Log.e(TAG, "query: invalid request: " + uri);
                return null;
        }

        String finalSortOrder = null;
        if (TextUtils.isEmpty(sortOrder)) {
            if (qb.getTables().equals(TABLE_PDU)) {
                finalSortOrder = Mms.DATE + " DESC";
            } else if (qb.getTables().equals(TABLE_PART)) {
                finalSortOrder = Part.SEQ;
            }
        } else {
            finalSortOrder = sortOrder;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projection, selection,
                selectionArgs, null, null, finalSortOrder);

        // TODO: Does this need to be a URI for this provider.
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    private void constructQueryForBox(SQLiteQueryBuilder qb, int msgBox) {
        qb.setTables(TABLE_PDU);

        if (msgBox != Mms.MESSAGE_BOX_ALL) {
            qb.appendWhere(Mms.MESSAGE_BOX + "=" + msgBox);
        }
    }

    @Override
    public String getType(Uri uri) {
        int match = sURLMatcher.match(uri);
        switch (match) {
            case MMS_ALL:
            case MMS_INBOX:
            case MMS_SENT:
            case MMS_DRAFTS:
            case MMS_OUTBOX:
                return VND_ANDROID_DIR_MMS;
            case MMS_ALL_ID:
            case MMS_INBOX_ID:
            case MMS_SENT_ID:
            case MMS_DRAFTS_ID:
            case MMS_OUTBOX_ID:
                return VND_ANDROID_MMS;
            case MMS_PART_ID: {
                Cursor cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_PART, new String[] { Part.CONTENT_TYPE },
                        Part._ID + " = ?", new String[] { uri.getLastPathSegment() },
                        null, null, null);
                if (cursor != null) {
                    try {
                        if ((cursor.getCount() == 1) && cursor.moveToFirst()) {
                            return cursor.getString(0);
                        } else {
                            Log.e(TAG, "cursor.count() != 1: " + uri);
                        }
                    } finally {
                        cursor.close();
                    }
                } else {
                    Log.e(TAG, "cursor == null: " + uri);
                }
                return "*/*";
            }
            case MMS_ALL_PART:
            case MMS_MSG_PART:
            case MMS_MSG_ADDR:
            default:
                return "*/*";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int msgBox = Mms.MESSAGE_BOX_ALL;
        boolean notify = true;

        int match = sURLMatcher.match(uri);
        if (LOCAL_LOGV) {
            Log.v(TAG, "Insert uri=" + uri + ", match=" + match);
        }

        String table = TABLE_PDU;
        switch (match) {
            case MMS_ALL:
                Object msgBoxObj = values.getAsInteger(Mms.MESSAGE_BOX);
                if (msgBoxObj != null) {
                    msgBox = (Integer) msgBoxObj;
                }
                else {
                    // default to inbox
                    msgBox = Mms.MESSAGE_BOX_INBOX;
                }
                break;
            case MMS_INBOX:
                msgBox = Mms.MESSAGE_BOX_INBOX;
                break;
            case MMS_SENT:
                msgBox = Mms.MESSAGE_BOX_SENT;
                break;
            case MMS_DRAFTS:
                msgBox = Mms.MESSAGE_BOX_DRAFTS;
                break;
            case MMS_OUTBOX:
                msgBox = Mms.MESSAGE_BOX_OUTBOX;
                break;
            case MMS_MSG_PART:
                notify = false;
                table = TABLE_PART;
                break;
            case MMS_MSG_ADDR:
                notify = false;
                table = TABLE_ADDR;
                break;
            case MMS_SENDING_RATE:
                notify = false;
                table = TABLE_RATE;
                break;
            case MMS_DRM_STORAGE:
                notify = false;
                table = TABLE_DRM;
                break;
            default:
                Log.e(TAG, "insert: invalid request: " + uri);
                return null;
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues finalValues;
        Uri res = Mms.CONTENT_URI;
        long rowId;

        if (table.equals(TABLE_PDU)) {
            boolean addDate = !values.containsKey(Mms.DATE);
            boolean addMsgBox = !values.containsKey(Mms.MESSAGE_BOX);

            // Filter keys we don't support yet.
            filterUnsupportedKeys(values);

            // TODO: Should initialValues be validated, e.g. if it
            // missed some significant keys?
            finalValues = new ContentValues(values);

            long timeInMillis = System.currentTimeMillis();

            if (addDate) {
                finalValues.put(Mms.DATE, timeInMillis / 1000L);
            }

            if (addMsgBox && (msgBox != Mms.MESSAGE_BOX_ALL)) {
                finalValues.put(Mms.MESSAGE_BOX, msgBox);
            }

            if (msgBox != Mms.MESSAGE_BOX_INBOX) {
                // Mark all non-inbox messages read.
                finalValues.put(Mms.READ, 1);
            }

            if ((rowId = db.insert(table, null, finalValues)) <= 0) {
                Log.e(TAG, "MmsProvider.insert: failed! " + finalValues);
                return null;
            }

            res = Uri.parse(res + "/" + rowId);

        } else if (table.equals(TABLE_ADDR)) {
            finalValues = new ContentValues(values);
            finalValues.put(Addr.MSG_ID, uri.getPathSegments().get(0));

            if ((rowId = db.insert(table, null, finalValues)) <= 0) {
                Log.e(TAG, "Failed to insert address: " + finalValues);
                return null;
            }

            res = Uri.parse(res + "/addr/" + rowId);
        } else if (table.equals(TABLE_PART)) {
            finalValues = new ContentValues(values);

            if (match == MMS_MSG_PART) {
                finalValues.put(Part.MSG_ID, uri.getPathSegments().get(0));
            }

            String contentType = values.getAsString("ct");
            
            // text/plain and app application/smil store their "data" inline in the
            // table so there's no need to create the file
            if (!"text/plain".equals(contentType) && !"application/smil".equals(contentType)) {
                // Generate the '_data' field of the part with default
                // permission settings.
                String path = getContext().getDir("parts", 0).getPath()
                + "/PART_" + System.currentTimeMillis();

                finalValues.put(Part._DATA, path);

                File partFile = new File(path);
                if (!partFile.exists()) {
                    try {
                        if (!partFile.createNewFile()) {
                            throw new IllegalStateException(
                                    "Unable to create new partFile: " + path);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "createNewFile", e);
                        throw new IllegalStateException(
                                "Unable to create new partFile: " + path);
                    }
                }
            }

            if ((rowId = db.insert(table, null, finalValues)) <= 0) {
                Log.e(TAG, "MmsProvider.insert: failed! " + finalValues);
                return null;
            }

            res = Uri.parse(res + "/part/" + rowId);
        } else if (table.equals(TABLE_RATE)) {
            long now = values.getAsLong(Rate.SENT_TIME);
            long oneHourAgo = now - 1000 * 60 * 60;
            // Delete all unused rows (time earlier than one hour ago).
            db.delete(table, Rate.SENT_TIME + "<=" + oneHourAgo, null);
            db.insert(table, null, values);
        } else if (table.equals(TABLE_DRM)) {
            String path = getContext().getDir("parts", 0).getPath()
                    + "/PART_" + System.currentTimeMillis();
            finalValues = new ContentValues(1);
            finalValues.put("_data", path);

            File partFile = new File(path);
            if (!partFile.exists()) {
                try {
                    if (!partFile.createNewFile()) {
                        throw new IllegalStateException(
                                "Unable to create new file: " + path);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "createNewFile", e);
                    throw new IllegalStateException(
                            "Unable to create new file: " + path);
                }
            }

            if ((rowId = db.insert(table, null, finalValues)) <= 0) {
                Log.e(TAG, "MmsProvider.insert: failed! " + finalValues);
                return null;
            }
            res = Uri.parse(res + "/drm/" + rowId);
        } else {
            throw new AssertionError("Unknown table type: " + table);
        }

        if (notify) {
            notifyChange();
        }
        return res;
    }

    private int getMessageBoxByMatch(int match) {
        switch (match) {
            case MMS_INBOX_ID:
            case MMS_INBOX:
                return Mms.MESSAGE_BOX_INBOX;
            case MMS_SENT_ID:
            case MMS_SENT:
                return Mms.MESSAGE_BOX_SENT;
            case MMS_DRAFTS_ID:
            case MMS_DRAFTS:
                return Mms.MESSAGE_BOX_DRAFTS;
            case MMS_OUTBOX_ID:
            case MMS_OUTBOX:
                return Mms.MESSAGE_BOX_OUTBOX;
            default:
                throw new IllegalArgumentException("bad Arg: " + match);
        }
    }

    @Override
    public int delete(Uri uri, String selection,
            String[] selectionArgs) {
        int match = sURLMatcher.match(uri);
        if (LOCAL_LOGV) {
            Log.v(TAG, "Delete uri=" + uri + ", match=" + match);
        }

        String table, extraSelection = null;
        boolean notify = false;

        switch (match) {
            case MMS_ALL_ID:
            case MMS_INBOX_ID:
            case MMS_SENT_ID:
            case MMS_DRAFTS_ID:
            case MMS_OUTBOX_ID:
                notify = true;
                table = TABLE_PDU;
                extraSelection = Mms._ID + "=" + uri.getLastPathSegment();
                break;
            case MMS_ALL:
            case MMS_INBOX:
            case MMS_SENT:
            case MMS_DRAFTS:
            case MMS_OUTBOX:
                notify = true;
                table = TABLE_PDU;
                if (match != MMS_ALL) {
                    int msgBox = getMessageBoxByMatch(match);
                    extraSelection = Mms.MESSAGE_BOX + "=" + msgBox;
                }
                break;
            case MMS_ALL_PART:
                table = TABLE_PART;
                break;
            case MMS_MSG_PART:
                table = TABLE_PART;
                extraSelection = Part.MSG_ID + "=" + uri.getPathSegments().get(0);
                break;
            case MMS_PART_ID:
                table = TABLE_PART;
                extraSelection = Part._ID + "=" + uri.getPathSegments().get(1);
                break;
            case MMS_MSG_ADDR:
                table = TABLE_ADDR;
                extraSelection = Addr.MSG_ID + "=" + uri.getPathSegments().get(0);
                break;
            case MMS_DRM_STORAGE:
                table = TABLE_DRM;
                break;
            default:
                Log.w(TAG, "No match for URI '" + uri + "'");
                return 0;
        }

        String finalSelection = concatSelections(selection, extraSelection);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int deletedRows = 0;

        if (TABLE_PDU.equals(table)) {
            deletedRows = deleteMessages(getContext(), db, finalSelection,
                                         selectionArgs, uri);
        } else if (TABLE_PART.equals(table)) {
            deletedRows = deleteParts(db, finalSelection, selectionArgs);
        } else if (TABLE_DRM.equals(table)) {
            deletedRows = deleteTempDrmData(db, finalSelection, selectionArgs);
        } else {
            deletedRows = db.delete(table, finalSelection, selectionArgs);
        }

        if ((deletedRows > 0) && notify) {
            notifyChange();
        }
        return deletedRows;
    }

    static int deleteMessages(Context context, SQLiteDatabase db,
            String selection, String[] selectionArgs, Uri uri) {
        Cursor cursor = db.query(TABLE_PDU, new String[] { Mms._ID },
                selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return 0;
        }

        try {
            if (cursor.getCount() == 0) {
                return 0;
            }

            while (cursor.moveToNext()) {
                deleteParts(db, Part.MSG_ID + " = ?",
                        new String[] { String.valueOf(cursor.getLong(0)) });
            }
        } finally {
            cursor.close();
        }

        int count = db.delete(TABLE_PDU, selection, selectionArgs);
        if (count > 0) {
            Intent intent = new Intent(Mms.Intents.CONTENT_CHANGED_ACTION);
            intent.putExtra(Mms.Intents.DELETED_CONTENTS, uri);
            if (LOCAL_LOGV) {
                Log.v(TAG, "Broadcasting intent: " + intent);
            }
            context.sendBroadcast(intent);
        }
        return count;
    }

    private static int deleteParts(SQLiteDatabase db, String selection,
            String[] selectionArgs) {
        return deleteDataRows(db, TABLE_PART, selection, selectionArgs);
    }

    private static int deleteTempDrmData(SQLiteDatabase db, String selection,
            String[] selectionArgs) {
        return deleteDataRows(db, TABLE_DRM, selection, selectionArgs);
    }

    private static int deleteDataRows(SQLiteDatabase db, String table,
            String selection, String[] selectionArgs) {
        Cursor cursor = db.query(table, new String[] { "_data" },
                selection, selectionArgs, null, null, null);
        if (cursor == null) {
            // FIXME: This might be an error, ignore it may cause
            // unpredictable result.
            return 0;
        }

        try {
            if (cursor.getCount() == 0) {
                return 0;
            }

            while (cursor.moveToNext()) {
                try {
                    // Delete the associated files saved on file-system.
                    String path = cursor.getString(0);
                    if (path != null) {
                        new File(path).delete();
                    }
                } catch (Throwable ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
            }
        } finally {
            cursor.close();
        }

        return db.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        int match = sURLMatcher.match(uri);
        if (LOCAL_LOGV) {
            Log.v(TAG, "Update uri=" + uri + ", match=" + match);
        }

        boolean notify = false;
        String msgId = null;
        String table;

        switch (match) {
            case MMS_ALL_ID:
            case MMS_INBOX_ID:
            case MMS_SENT_ID:
            case MMS_DRAFTS_ID:
            case MMS_OUTBOX_ID:
                msgId = uri.getLastPathSegment();
            // fall-through
            case MMS_ALL:
            case MMS_INBOX:
            case MMS_SENT:
            case MMS_DRAFTS:
            case MMS_OUTBOX:
                notify = true;
                table = TABLE_PDU;
                break;
            case MMS_MSG_PART:
            case MMS_PART_ID:
                table = TABLE_PART;
                break;
            default:
                Log.w(TAG, "Update operation for '" + uri + "' not implemented.");
                return 0;
        }

        String extraSelection = null;
        ContentValues finalValues;
        if (table.equals(TABLE_PDU)) {
            // Filter keys that we don't support yet.
            filterUnsupportedKeys(values);
            finalValues = new ContentValues(values);

            if (msgId != null) {
                extraSelection = Mms._ID + "=" + msgId;
            }
        } else if (table.equals(TABLE_PART)) {
            finalValues = new ContentValues(values);

            switch (match) {
                case MMS_MSG_PART:
                    extraSelection = Part.MSG_ID + "=" + uri.getPathSegments().get(0);
                    break;
                case MMS_PART_ID:
                    extraSelection = Part._ID + "=" + uri.getPathSegments().get(1);
                    break;
                default:
                    break;
            }
        } else {
            return 0;
        }

        String finalSelection = concatSelections(selection, extraSelection);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(table, finalValues, finalSelection, selectionArgs);
        if (notify && (count > 0)) {
            notifyChange();
        }
        return count;
    }

    private ParcelFileDescriptor getTempStoreFd() {
        String fileName = Mms.ScrapSpace.SCRAP_FILE_PATH;
        ParcelFileDescriptor pfd = null;

        try {
            File file = new File(fileName);

            // make sure the path is valid and directories created for this file.
            File parentFile = file.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                Log.e(TAG, "[MmsProvider] getTempStoreFd: " + parentFile.getPath() +
                        "does not exist!");
                return null;
            }

            pfd = ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_READ_WRITE
                            | android.os.ParcelFileDescriptor.MODE_CREATE);
        } catch (Exception ex) {
            Log.e(TAG, "getTempStoreFd: error creating pfd for " + fileName, ex);
        }

        return pfd;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        // if the url is "content://mms/takePictureTempStore", then it means the requester
        // wants a file descriptor to write image data to.

        ParcelFileDescriptor fd;
        int match = sURLMatcher.match(uri);

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.d(TAG, "openFile: uri=" + uri + ", mode=" + mode);
        }

        switch (match) {
            case MMS_SCRAP_SPACE:
                fd = getTempStoreFd();
                break;

            default:
                fd = openFileHelper(uri, mode);
        }

        return fd;
    }

    private void filterUnsupportedKeys(ContentValues values) {
        // Some columns are unsupported.  They should therefore
        // neither be inserted nor updated.  Filter them out.
        values.remove(Mms.DELIVERY_TIME_TOKEN);
        values.remove(Mms.SENDER_VISIBILITY);
        values.remove(Mms.REPLY_CHARGING);
        values.remove(Mms.REPLY_CHARGING_DEADLINE_TOKEN);
        values.remove(Mms.REPLY_CHARGING_DEADLINE);
        values.remove(Mms.REPLY_CHARGING_ID);
        values.remove(Mms.REPLY_CHARGING_SIZE);
        values.remove(Mms.PREVIOUSLY_SENT_BY);
        values.remove(Mms.PREVIOUSLY_SENT_DATE);
        values.remove(Mms.STORE);
        values.remove(Mms.MM_STATE);
        values.remove(Mms.MM_FLAGS_TOKEN);
        values.remove(Mms.MM_FLAGS);
        values.remove(Mms.STORE_STATUS);
        values.remove(Mms.STORE_STATUS_TEXT);
        values.remove(Mms.STORED);
        values.remove(Mms.TOTALS);
        values.remove(Mms.MBOX_TOTALS);
        values.remove(Mms.MBOX_TOTALS_TOKEN);
        values.remove(Mms.QUOTAS);
        values.remove(Mms.MBOX_QUOTAS);
        values.remove(Mms.MBOX_QUOTAS_TOKEN);
        values.remove(Mms.MESSAGE_COUNT);
        values.remove(Mms.START);
        values.remove(Mms.DISTRIBUTION_INDICATOR);
        values.remove(Mms.ELEMENT_DESCRIPTOR);
        values.remove(Mms.LIMIT);
        values.remove(Mms.RECOMMENDED_RETRIEVAL_MODE);
        values.remove(Mms.RECOMMENDED_RETRIEVAL_MODE_TEXT);
        values.remove(Mms.STATUS_TEXT);
        values.remove(Mms.APPLIC_ID);
        values.remove(Mms.REPLY_APPLIC_ID);
        values.remove(Mms.AUX_APPLIC_ID);
        values.remove(Mms.DRM_CONTENT);
        values.remove(Mms.ADAPTATION_ALLOWED);
        values.remove(Mms.REPLACE_ID);
        values.remove(Mms.CANCEL_ID);
        values.remove(Mms.CANCEL_STATUS);

        // Keys shouldn't be inserted or updated.
        values.remove(Mms._ID);
    }

    private void notifyChange() {
        getContext().getContentResolver().notifyChange(
                MmsSms.CONTENT_URI, null);
    }

    private final static String TAG = "MmsProvider";
    private final static String VND_ANDROID_MMS = "vnd.android/mms";
    private final static String VND_ANDROID_DIR_MMS = "vnd.android-dir/mms";
    private final static boolean DEBUG = false;
    private final static boolean LOCAL_LOGV = DEBUG ? Config.LOGD : Config.LOGV;

    private static final int MMS_ALL                      = 0;
    private static final int MMS_ALL_ID                   = 1;
    private static final int MMS_INBOX                    = 2;
    private static final int MMS_INBOX_ID                 = 3;
    private static final int MMS_SENT                     = 4;
    private static final int MMS_SENT_ID                  = 5;
    private static final int MMS_DRAFTS                   = 6;
    private static final int MMS_DRAFTS_ID                = 7;
    private static final int MMS_OUTBOX                   = 8;
    private static final int MMS_OUTBOX_ID                = 9;
    private static final int MMS_ALL_PART                 = 10;
    private static final int MMS_MSG_PART                 = 11;
    private static final int MMS_PART_ID                  = 12;
    private static final int MMS_MSG_ADDR                 = 13;
    private static final int MMS_SENDING_RATE             = 14;
    private static final int MMS_REPORT_STATUS            = 15;
    private static final int MMS_REPORT_REQUEST           = 16;
    private static final int MMS_DRM_STORAGE              = 17;
    private static final int MMS_DRM_STORAGE_ID           = 18;
    private static final int MMS_THREADS                  = 19;
    private static final int MMS_SCRAP_SPACE              = 20;

    private static final UriMatcher
            sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("mms", null,         MMS_ALL);
        sURLMatcher.addURI("mms", "#",          MMS_ALL_ID);
        sURLMatcher.addURI("mms", "inbox",      MMS_INBOX);
        sURLMatcher.addURI("mms", "inbox/#",    MMS_INBOX_ID);
        sURLMatcher.addURI("mms", "sent",       MMS_SENT);
        sURLMatcher.addURI("mms", "sent/#",     MMS_SENT_ID);
        sURLMatcher.addURI("mms", "drafts",     MMS_DRAFTS);
        sURLMatcher.addURI("mms", "drafts/#",   MMS_DRAFTS_ID);
        sURLMatcher.addURI("mms", "outbox",     MMS_OUTBOX);
        sURLMatcher.addURI("mms", "outbox/#",   MMS_OUTBOX_ID);
        sURLMatcher.addURI("mms", "part",       MMS_ALL_PART);
        sURLMatcher.addURI("mms", "#/part",     MMS_MSG_PART);
        sURLMatcher.addURI("mms", "part/#",     MMS_PART_ID);
        sURLMatcher.addURI("mms", "#/addr",     MMS_MSG_ADDR);
        sURLMatcher.addURI("mms", "rate",       MMS_SENDING_RATE);
        sURLMatcher.addURI("mms", "report-status/#",  MMS_REPORT_STATUS);
        sURLMatcher.addURI("mms", "report-request/#", MMS_REPORT_REQUEST);
        sURLMatcher.addURI("mms", "drm",        MMS_DRM_STORAGE);
        sURLMatcher.addURI("mms", "drm/#",      MMS_DRM_STORAGE_ID);
        sURLMatcher.addURI("mms", "threads",    MMS_THREADS);
        sURLMatcher.addURI("mms", "scrapSpace", MMS_SCRAP_SPACE);
    }

    private SQLiteOpenHelper mOpenHelper;

    private static String concatSelections(String selection1, String selection2) {
        if (TextUtils.isEmpty(selection1)) {
            return selection2;
        } else if (TextUtils.isEmpty(selection2)) {
            return selection1;
        } else {
            return selection1 + " AND " + selection2;
        }
    }
}

