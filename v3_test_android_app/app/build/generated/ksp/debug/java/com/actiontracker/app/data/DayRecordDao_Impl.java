package com.actiontracker.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.actiontracker.app.models.DayRecordEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DayRecordDao_Impl implements DayRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DayRecordEntity> __insertionAdapterOfDayRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllRecordsForAction;

  public DayRecordDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDayRecordEntity = new EntityInsertionAdapter<DayRecordEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `day_records` (`date`,`actionId`,`count`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DayRecordEntity value) {
        if (value.getDate() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getDate());
        }
        stmt.bindLong(2, value.getActionId());
        stmt.bindLong(3, value.getCount());
      }
    };
    this.__preparedStmtOfDeleteAllRecordsForAction = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM day_records WHERE actionId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdateDayRecord(final DayRecordEntity dayRecordEntity,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDayRecordEntity.insert(dayRecordEntity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object incrementCount(final String date, final int actionId,
      final Continuation<? super Unit> continuation) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> DayRecordDao.DefaultImpls.incrementCount(DayRecordDao_Impl.this, date, actionId, __cont), continuation);
  }

  @Override
  public Object decrementCount(final String date, final int actionId,
      final Continuation<? super Unit> continuation) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> DayRecordDao.DefaultImpls.decrementCount(DayRecordDao_Impl.this, date, actionId, __cont), continuation);
  }

  @Override
  public Object deleteAllRecordsForAction(final int actionId,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllRecordsForAction.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, actionId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAllRecordsForAction.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public LiveData<List<DayRecordEntity>> getDayRecordsForDate(final String date) {
    final String _sql = "SELECT * FROM day_records WHERE date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"day_records"}, false, new Callable<List<DayRecordEntity>>() {
      @Override
      public List<DayRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfActionId = CursorUtil.getColumnIndexOrThrow(_cursor, "actionId");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final List<DayRecordEntity> _result = new ArrayList<DayRecordEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DayRecordEntity _item;
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final int _tmpActionId;
            _tmpActionId = _cursor.getInt(_cursorIndexOfActionId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new DayRecordEntity(_tmpDate,_tmpActionId,_tmpCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDayRecordForDateAndAction(final String date, final int actionId,
      final Continuation<? super DayRecordEntity> continuation) {
    final String _sql = "SELECT * FROM day_records WHERE date = ? AND actionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, actionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DayRecordEntity>() {
      @Override
      public DayRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfActionId = CursorUtil.getColumnIndexOrThrow(_cursor, "actionId");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final DayRecordEntity _result;
          if(_cursor.moveToFirst()) {
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final int _tmpActionId;
            _tmpActionId = _cursor.getInt(_cursorIndexOfActionId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _result = new DayRecordEntity(_tmpDate,_tmpActionId,_tmpCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
