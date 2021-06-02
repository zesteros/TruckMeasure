package com.mx.vise.cubicaciones.dao;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.entities.CubagePhotos;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.pojos.BrandPOJO;
import com.mx.vise.cubicaciones.pojos.CubagePOJO;
import com.mx.vise.cubicaciones.pojos.CubageProcessPOJO;
import com.mx.vise.cubicaciones.pojos.SyndicatePOJO;
import com.mx.vise.cubicaciones.pojos.TagPOJO;
import com.mx.vise.cubicaciones.tasks.Progress;
import com.mx.vise.cubicaciones.tasks.SyncData;
import com.mx.vise.cubicaciones.util.ColorUtils;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el miércoles 02 de mayo del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version CombustibleVISEandroidv1.0
 */

public class DAO {
    private static final String TAG = "VISE";
    private final Context mContext;
    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;
    private static String[] syndicates;

    public DAO(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(context);
    }

    /**
     * @return si se ha agregado
     */
    public static boolean addSyncDataToDatabase(CubagePOJO cubage, Context context, SyncData syncData) {
        /*Si la respuesta ya existe en la base de datos*/
        SQLiteDatabase.loadLibs(context);
        mDbHelper = new DatabaseHelper(context);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int totalElements = cubage.getBrands().size() + cubage.getSyndicates().size() + cubage.getTags().size();

        syncData.publishProgress(0, 0, totalElements);

        mDb = mDbHelper.getWritableDatabase(DatabaseHelper.DATABASE_K);

        mDbHelper.onUpgrade(mDb, 0, 0);

        int i = 0;

        for (BrandPOJO brand : cubage.getBrands()) {
            ContentValues values = new ContentValues();
            values.put(Contract.SCRIPT.COLUMN_ID_BRAND, brand.getId());
            values.put(Contract.SCRIPT.COLUMN_NAME_BRAND, brand.getName());
            values.put(Contract.SCRIPT.COLUMN_ADD_DATE, dateFormat.format(new Date()));
            mDb.insert(Contract.SCRIPT.TABLE_NAME_BRANDS, null, values);
            i++;
            syncData.publishProgress((int) (i * 100 / totalElements), i, totalElements);
        }

        for (SyndicatePOJO syndicate : cubage.getSyndicates()) {
            ContentValues values = new ContentValues();
            values.put(Contract.SCRIPT.COLUMN_ID_SYNDICATE, syndicate.getId());
            values.put(Contract.SCRIPT.COLUMN_NAME_SYNDICATE, syndicate.getName());
            values.put(Contract.SCRIPT.COLUMN_ADD_DATE, dateFormat.format(new Date()));
            mDb.insert(Contract.SCRIPT.TABLE_NAME_SYNDICATES, null, values);
            i++;
            syncData.publishProgress((int) (i * 100 / totalElements), i, totalElements);
        }

        for (TagPOJO tag : cubage.getTags()) {
            ContentValues values = new ContentValues();
            values.put(Contract.SCRIPT.COLUMN_ID_ACTIVO_TAG, tag.getIdActivo());
            values.put(Contract.SCRIPT.COLUMN_TID_TAG, tag.getTag());
            values.put(Contract.SCRIPT.COLUMN_ADD_DATE_TAG, dateFormat.format(tag.getAddDate()));
            values.put(Contract.SCRIPT.COLUMN_SYNC_DATE_TAG, dateFormat.format(new Date()));
            mDb.insert(Contract.SCRIPT.TABLE_NAME_TAGS, null, values);
            i++;
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
        }
        mDb.close();


        return false;

    }

    /**
     * @param context
     * @return
     */
    public static ArrayList<SyndicatePOJO> getSyndicates(Context context) {
        ArrayList<SyndicatePOJO> syndicates = new ArrayList<>();
        //syndicates.add(context.getString(R.string.select_a_syndicate));
        SyndicatePOJO firstItem = new SyndicatePOJO();
        firstItem.setId(0);
        firstItem.setName(context.getString(R.string.select_a_syndicate));
        syndicates.add(firstItem);

        SQLiteDatabase.loadLibs(context);
        mDbHelper = new DatabaseHelper(context);
        try {
            mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_SYNDICATES,   // The table to query
                null,             // The array of columns to return (pass null to key all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                Contract.SCRIPT.COLUMN_NAME_SYNDICATE + " asc"               // The sort order
        );

        if (cursor.getCount() == 0) return null;

        while (cursor.moveToNext()) {
            SyndicatePOJO syndicatePOJO = new SyndicatePOJO();
            syndicatePOJO.setId(cursor.getInt(0));
            syndicatePOJO.setName(cursor.getString(1));
            syndicates.add(syndicatePOJO);
        }

        cursor.close();
        mDb.close();

        return syndicates;
    }

    /**
     * @param context
     * @return
     */
    public static ArrayList<BrandPOJO> getBrands(Context context) {
        ArrayList<BrandPOJO> brands = new ArrayList<>();

        BrandPOJO firstBrand = new BrandPOJO();
        firstBrand.setId(0);
        firstBrand.setName(context.getString(R.string.select_a_brand));

        brands.add(firstBrand);

        SQLiteDatabase.loadLibs(context);
        mDbHelper = new DatabaseHelper(context);
        try {
            mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);
        } catch (SQLException e) {
            e.printStackTrace();
            return brands;
        }

        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_BRANDS,   // The table to query
                null,             // The array of columns to return (pass null to key all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                Contract.SCRIPT.COLUMN_NAME_BRAND + " asc"               // The sort order
        );
        while (cursor.moveToNext()) {
            BrandPOJO brandPOJO = new BrandPOJO();
            brandPOJO.setId(cursor.getInt(0));
            brandPOJO.setName(cursor.getString(1));
            brands.add(brandPOJO);
        }
        cursor.close();
        mDb.close();

        return brands;
    }

    /**
     * @param context the context
     * @param tid     the tid to verify if exists
     * @return if tag exists in database
     */
    public static boolean tagExists(Context context, String tid) {
        SQLiteDatabase.loadLibs(context);
        mDbHelper = new DatabaseHelper(context);
        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);

        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_TAGS,   // The table to query
                null,// The array of columns to return (pass null to get all)
                Contract.SCRIPT.COLUMN_TID_TAG + "=?",// The columns for the WHERE clause
                new String[]{tid},          // The values for the WHERE clause
                null,// don't group the rows
                null,// don't filter by row groups
                null// The sort order
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        mDb.close();
        return exists;
    }

    public static String getLastUpdateDate(Context context) {
        SQLiteDatabase.loadLibs(context);
        mDbHelper = new DatabaseHelper(context);
        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);

        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_SYNDICATES,   // The table to query
                new String[]{Contract.SCRIPT.COLUMN_ADD_DATE},// The array of columns to return (pass null to get all)
                null,// The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,// don't group the rows
                null,// don't filter by row groups
                Contract.SCRIPT.COLUMN_ADD_DATE + " desc"// The sort order
        );
        String lastUpdateDate = null;
        if (cursor.moveToFirst())
            lastUpdateDate = cursor.getString(0);
        cursor.close();
        mDb.close();
        return lastUpdateDate;
    }

    /**
     * @param context    The context
     * @param flowObject the flow object to extract
     * @return if save was successful
     */
    public static boolean saveCubage(Context context, FlowObject flowObject) {
        SQLiteDatabase.loadLibs(context);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        mDbHelper = new DatabaseHelper(context);
        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);

        ContentValues values = new ContentValues();
        values.put(Contract.SCRIPT.COLUMN_SHEET_NUMBER_CUBAGE, flowObject.getSheetNumber());
        values.put(Contract.SCRIPT.COLUMN_ID_SYNDICATE_CUBAGE, flowObject.getSelectedSyndicate().getId());
        values.put(Contract.SCRIPT.COLUMN_NAME_SYNDICATE_CUBAGE, flowObject.getSelectedSyndicate().getName());
        values.put(Contract.SCRIPT.COLUMN_OPERATOR_NAME_CUBAGE, flowObject.getWritedOperator());
        values.put(Contract.SCRIPT.COLUMN_ID_BUILDING_CUBAGE, flowObject.getSelectedBuilding()
                .split("-")[0] + "-" + flowObject.getSelectedBuilding().split("-")[1]);
        values.put(Contract.SCRIPT.COLUMN_REAR_PLATE_PATH_CUBAGE, flowObject.getRearPlatePhotoPath());
        values.put(Contract.SCRIPT.COLUMN_FRONT_PLATE_PATH_CUBAGE, flowObject.getFrontPlatePhotoPath());
        values.put(Contract.SCRIPT.COLUMN_LICENSE_CARD_PATH_CUBAGE, flowObject.getLicenceCardPhotoPath());
        values.put(Contract.SCRIPT.COLUMN_FRONT_PLATE_WRITED_CUBAGE, flowObject.getFrontPlateManual());
        values.put(Contract.SCRIPT.COLUMN_REAR_PLATE_WRITED_CUBAGE, flowObject.getRearPlateManual());
        values.put(Contract.SCRIPT.COLUMN_UNIT_TYPE_CUBAGE, flowObject.getSelectedUnitType());
        values.put(Contract.SCRIPT.COLUMN_BOX_COLOR_CUBAGE, flowObject.getSelectedBoxColor());
        values.put(Contract.SCRIPT.COLUMN_TRACTOR_COLOR_CUBAGE, flowObject.getSelectedTractorColor());
        values.put(Contract.SCRIPT.COLUMN_ID_BRAND_CUBAGE, flowObject.getSelectedBrand().getId());
        values.put(Contract.SCRIPT.COLUMN_UNIT_FRONTAL_PHOTO_PATH_CUBAGE, flowObject.getUnitFrontalPhotoPath());
        values.put(Contract.SCRIPT.COLUMN_UNIT_SIDE_PHOTO_PATH_CUBAGE, flowObject.getUnitSidePhotoPath());
        values.put(Contract.SCRIPT.COLUMN_UNIT_BACK_PHOTO_PATH_CUBAGE, flowObject.getUnitBackPhotoPath());
        values.put(Contract.SCRIPT.COLUMN_CIRCULATION_CARD_WRITED_CUBAGE, flowObject.getCirculationCardInserted());
        values.put(Contract.SCRIPT.COLUMN_CIRCULATION_CARD_PHOTO_PATH_CUBAGE, flowObject.getCirculationCardPhotoPath());
        values.put(Contract.SCRIPT.COLUMN_HAS_INCREASE_CUBAGE, flowObject.haveAnIncrease() ? 1 : 0);
        values.put(Contract.SCRIPT.COLUMN_L1_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[0] : 0);
        values.put(Contract.SCRIPT.COLUMN_A1_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[1] : 0);
        values.put(Contract.SCRIPT.COLUMN_H1_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[2] : 0);
        values.put(Contract.SCRIPT.COLUMN_L2_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[3] : 0);
        values.put(Contract.SCRIPT.COLUMN_A2_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[4] : 0);
        values.put(Contract.SCRIPT.COLUMN_H2_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[5] : 0);
        values.put(Contract.SCRIPT.COLUMN_LC_CUBAGE, flowObject.getBasicMeasures() != null ? flowObject.getBasicMeasures()[6] : 0);
        values.put(Contract.SCRIPT.COLUMN_L_CUBAGE, flowObject.getHydraulicJackDimensions() != null ? flowObject.getHydraulicJackDimensions()[0] : 0);
        values.put(Contract.SCRIPT.COLUMN_A_CUBAGE, flowObject.getHydraulicJackDimensions() != null ? flowObject.getHydraulicJackDimensions()[1] : 0);
        values.put(Contract.SCRIPT.COLUMN_H_CUBAGE, flowObject.getHydraulicJackDimensions() != null ? flowObject.getHydraulicJackDimensions()[2] : 0);
        values.put(Contract.SCRIPT.COLUMN_ADJUSTMENT_CUBAGE, flowObject.getAdjustment());
        values.put(Contract.SCRIPT.COLUMN_INCREASE_CUBAGE, flowObject.getIncrease());
        values.put(Contract.SCRIPT.COLUMN_OPERATOR_SIGNATURE_PATH_CUBAGE, flowObject.getOperatorSignaturePath());
        values.put(Contract.SCRIPT.COLUMN_USER_SIGNATURE_PATH_CUBAGE, flowObject.getUserSignaturePath());
        values.put(Contract.SCRIPT.COLUMN_ADD_USER_CUBAGE, flowObject.getSession().getEmployeeId());
        values.put(Contract.SCRIPT.COLUMN_STATUS_CUBAGE, "A");
        values.put(Contract.SCRIPT.COLUMN_OBSERVATIONS_CUBAGE, flowObject.getObservations());
        values.put(Contract.SCRIPT.COLUMN_ADD_DATE, dateFormat.format(new Date()));
        values.put(Contract.SCRIPT.COLUMN_BRAND_NAME_CUBAGE, flowObject.getSelectedBrand().getName());
        values.put(Contract.SCRIPT.COLUMN_TOTAL_VOLUME, flowObject.getTotalVolume());
        values.put(Contract.SCRIPT.COLUMN_REAR_PLATE_OCR, flowObject.getRearPlateByOCR());
        values.put(Contract.SCRIPT.COLUMN_FRONT_PLATE_OCR, flowObject.getFrontPlateByOCR());
        if(flowObject.getTag() != null)
            values.put(Contract.SCRIPT.COLUMN_TAG, flowObject.getTag());
        if(flowObject.getStartCaptureDate() != null)
            values.put(Contract.SCRIPT.COLUMN_START_CAPTURE_DATE, dateFormat.format(flowObject.getStartCaptureDate()));
        if(flowObject.getEndCaptureDate() != null)
            values.put(Contract.SCRIPT.COLUMN_END_CAPTURE_DATE, dateFormat.format(flowObject.getEndCaptureDate()));

        long insertSucces = mDb.insert(Contract.SCRIPT.TABLE_NAME_CUBAGE, null, values);

        mDb.close();

        return insertSucces != -1;
    }

    public static boolean thereIsCubagesToSend(Context context){
        mDbHelper = new DatabaseHelper(context);

        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_CUBAGE,   // The table to query
                null,// ,// The array of columns to return (pass null to get all)
                Contract.SCRIPT.COLUMN_STATUS_CUBAGE + "=?",// The columns for the WHERE clause
                new String[]{"A"},          // The values for the WHERE clause
                null,// don't group the rows
                null,// don't filter by row groups
                null// The sort order
        );
        int totalElements = cursor.getCount();
        return totalElements > 0;
    }

    public static List<CubageProcessPOJO> getCubageToSend(Context context, SyncData syncData, ArrayList<CubagePhotos> cubagesDispatched) {
        List<CubageProcessPOJO> cubageProcessPOJOS = new ArrayList();

        SQLiteDatabase.loadLibs(context);

        mDbHelper = new DatabaseHelper(context);

        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_CUBAGE,   // The table to query
                null,// ,// The array of columns to return (pass null to get all)
                Contract.SCRIPT.COLUMN_STATUS_CUBAGE + "=?",// The columns for the WHERE clause
                new String[]{"A"},          // The values for the WHERE clause
                null,// don't group the rows
                null,// don't filter by row groups
                null// The sort order
        );
        int totalElements = cursor.getCount()*9;
        syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando imágenes..."));
        int i = 0;
        ImageHelper imageHelper = new ImageHelper();
        while (cursor.moveToNext()) {
            CubagePhotos cubageDispatched = new CubagePhotos();
            CubageProcessPOJO cubageProcessPOJO = new CubageProcessPOJO();

            cubageProcessPOJO.setSheetNumber(cursor.getString(1));
            cubageDispatched.setSheetNumber(cursor.getString(1));
            cubageProcessPOJO.setIdSyndicate(cursor.getInt(2));
            cubageProcessPOJO.setSyndicateName(cursor.getString(3));
            cubageProcessPOJO.setOperator(cursor.getString(4));
            cubageProcessPOJO.setIdBuilding(cursor.getString(5));

            cubageProcessPOJO.setRearPlatePhoto(getPhoto(cursor.getString(6)));

            cubageDispatched.setRearPlate(cursor.getString(6));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setFrontPlatePhoto(getPhoto(cursor.getString(7)));
            cubageDispatched.setFrontalPlate(cursor.getString(7));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setLicenseCardPhoto(getPhoto(cursor.getString(8)));
            cubageDispatched.setLicenceCardPath(cursor.getString(8));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setFrontalPlate(cursor.getString(9));
            cubageProcessPOJO.setRearPlate(cursor.getString(10));
            cubageProcessPOJO.setUnitType(cursor.getString(11));
            cubageProcessPOJO.setBoxColor(ColorUtils.getColorNameFromHex(cursor.getString(12)));
            cubageProcessPOJO.setTractorColor(ColorUtils.getColorNameFromHex(cursor.getString(13)));
            cubageProcessPOJO.setIdBrand(cursor.getInt(14));

//
//            ImageHelper.convertBitmapToByteWrapper(imageHelper.convertImageToBitmapFromPath(cursor.getString(15)));
            cubageProcessPOJO.setFrontalUnitPhoto(getPhoto(cursor.getString(15)));
            cubageDispatched.setFrontalPhoto(cursor.getString(15));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setSideUnitPhoto(getPhoto(cursor.getString(16)));
            cubageDispatched.setSidePhoto(cursor.getString(16));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setBackUnitPhoto(getPhoto(cursor.getString(17)));
            cubageDispatched.setBackPhoto(cursor.getString(17));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setCirculationCard(cursor.getString(18));
            cubageDispatched.setCirculationCardPhoto(cursor.getString(18));
            cubageProcessPOJO.setCirculationCardPhoto(getPhoto(cursor.getString(19)));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setHasIncrease(cursor.getInt(20) == 1);
            cubageProcessPOJO.setL1(cursor.getFloat(21));
            cubageProcessPOJO.setA1(cursor.getFloat(22));
            cubageProcessPOJO.setH1(cursor.getFloat(23));
            cubageProcessPOJO.setL2(cursor.getFloat(24));
            cubageProcessPOJO.setA2(cursor.getFloat(25));
            cubageProcessPOJO.setH2(cursor.getFloat(26));
            cubageProcessPOJO.setLc(cursor.getFloat(27));
            cubageProcessPOJO.setL(cursor.getFloat(28));
            cubageProcessPOJO.setA(cursor.getFloat(29));
            cubageProcessPOJO.setH(cursor.getFloat(30));
            cubageProcessPOJO.setAdjustment(cursor.getFloat(31));
            cubageProcessPOJO.setIncrease(cursor.getFloat(32));
            cubageProcessPOJO.setOperatorSignaturePhoto(ImageHelper.convertBitmapToByteWrapper(imageHelper.convertImageToBitmapFromPath(cursor.getString(33), 868, 512)));
            cubageDispatched.setOperatorSignaturePhoto(cursor.getString(33));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setUserSignaturePhoto(ImageHelper.convertBitmapToByteWrapper(imageHelper.convertImageToBitmapFromPath(cursor.getString(34), 868, 512)));
            cubageDispatched.setUserSignaturePhoto(cursor.getString(34));
            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
            i++;
            cubageProcessPOJO.setAddUser(cursor.getInt(35));
            cubageProcessPOJO.setObservations(cursor.getString(37));
            try {
                cubageProcessPOJO.setAddDate(dateFormat.parse(cursor.getString(38)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cubageProcessPOJO.setBrandName(cursor.getString(39));
            cubageProcessPOJO.setVolumePipeOrGondola(cursor.getFloat(40));
            cubageProcessPOJO.setRearPlateReadByOCR(cursor.getString(41));
            cubageProcessPOJO.setFrontPlateReadByOCR(cursor.getString(42));

            if(cursor.getString(43) != null)
                cubageProcessPOJO.setTag(cursor.getString(43));

            cubageProcessPOJOS.add(cubageProcessPOJO);
            cubagesDispatched.add(cubageDispatched);

        }
        cursor.close();
        mDb.close();

        return cubageProcessPOJOS;
    }
    public static Byte[] getPhoto(String path){
        return ImageHelper.convertToByteWrapper(convertImageFromPathToByteArray(path));
    }
    public static byte[] convertImageFromPathToByteArray(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        return bao.toByteArray();
    }

    /**
     * @param context el contexto
     * @param status el estatus a poner
     * @param cubage la cubicacion (fotos)
     * @return si se ha cambiado exitosamente
     */
    public static boolean changeCubageStatus(Context context, String status, CubagePhotos cubage) {

        SQLiteDatabase.loadLibs(context);

        mDbHelper = new DatabaseHelper(context);

        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(Contract.SCRIPT.COLUMN_STATUS_CUBAGE, status);

        // Which row to update, based on the title
        String selection = Contract.SCRIPT.COLUMN_SHEET_NUMBER_CUBAGE + " = ?";
        String[] selectionArgs = {cubage.getSheetNumber()};

        int count = mDb.update(
                Contract.SCRIPT.TABLE_NAME_CUBAGE,
                values,
                selection,
                selectionArgs);
        boolean success = count > 0;

        if (success) {
            for (int i = 0; i < cubage.getPhotos().length; i++)
                if(ImageHelper.deleteImage(cubage.getPhotos()[i]))
                    Log.i(TAG, "changeCubageStatus: imagen eliminada ="+cubage.getPhotos()[i]);
        }
        mDb.close();
        return success;
    }

    public static int getAmountOfCubages(Context context) {
        SQLiteDatabase.loadLibs(context);

        mDbHelper = new DatabaseHelper(context);

        mDb = mDbHelper.getReadableDatabase(DatabaseHelper.DATABASE_K);
        Cursor cursor = mDb.query(
                Contract.SCRIPT.TABLE_NAME_CUBAGE,   // The table to query
                null,// ,// The array of columns to return (pass null to get all)
                Contract.SCRIPT.COLUMN_STATUS_CUBAGE + "=?",// The columns for the WHERE clause
                new String[]{"A"},          // The values for the WHERE clause
                null,// don't group the rows
                null,// don't filter by row groups
                null// The sort order
        );

        return cursor.getCount();
    }
}