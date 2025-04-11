package com.actiontracker.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.actiontracker.app.models.ActionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
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
public final class ActionDao_Impl implements ActionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ActionEntity> __insertionAdapterOfActionEntity;

  private final EntityDeletionOrUpdateAdapter<ActionEntity> __deletionAdapterOfActionEntity;

  private final EntityDeletionOrUpdateAdapter<ActionEntity> __updateAdapterOfActionEntity;

  public ActionDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfActionEntity = new EntityInsertionAdapter<ActionEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `actions` (`actionId`,`actionName`,`creationTimestamp`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ActionEntity value) {
        stmt.bindLong(1, value.getActionId());
        if (value.getActionName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getActionName());
        }
        stmt.bindLong(3, value.getCreationTimestamp());
      }
    };
    this.__deletionAdapterOfActionEntity = new EntityDeletionOrUpdateAdapter<ActionEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `actions` WHERE `actionId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ActionEntity value) {
        stmt.bindLong(1, value.getActionId());
      }
    };
    this.__updateAdapterOfActionEntity = new EntityDeletionOrUpdateAdapter<ActionEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `actions` SET `actionId` = ?,`actionName` = ?,`creationTimestamp` = ? WHERE `actionId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ActionEntity value) {
        stmt.bindLong(1, value.getActionId());
        if (value.getActionName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getActionName());
        }
        stmt.bindLong(3, value.getCreationTimestamp());
        stmt.bindLong(4, value.getActionId());
      }
    };
  }

  @Override
  public Object insertAction(final ActionEntity actionEntity,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfActionEntity.insertAndReturnId(actionEntity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteAction(final ActionEntity actionEntity,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfActionEntity.handle(actionEntity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateAction(final ActionEntity actionEntity,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfActionEntity.handle(actionEntity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public LiveData<List<ActionEntity>> getAllActions() {
    final String _sql = "SELECT * FROM actions ORDER BY creationTimestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"actions"}, false, new Callable<List<ActionEntity>>() {
      @Override
      public List<ActionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfActionId = CursorUtil.getColumnIndexOrThrow(_cursor, "actionId");
          final int _cursorIndexOfActionName = CursorUtil.getColumnIndexOrThrow(_cursor, "actionName");
          final int _cursorIndexOfCreationTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "creationTimestamp");
          final List<ActionEntity> _result = new ArrayList<ActionEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ActionEntity _item;
            final int _tmpActionId;
            _tmpActionId = _cursor.getInt(_cursorIndexOfActionId);
            final String _tmpActionName;
            if (_cursor.isNull(_cursorIndexOfActionName)) {
              _tmpActionName = null;
            } else {
              _tmpActionName = _cursor.getString(_cursorIndexOfActionName);
            }
            final long _tmpCreationTimestamp;
            _tmpCreationTimestamp = _cursor.getLong(_cursorIndexOfCreationTimestamp);
            _item = new ActionEntity(_tmpActionId,_tmpActionName,_tmpCreationTimestamp);
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
  public Object getActionById(final int actionId,
      final Continuation<? super ActionEntity> continuation) {
    final String _sql = "SELECT * FROM actions WHERE actionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, actionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ActionEntity>() {
      @Override
      public ActionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfActionId = CursorUtil.getColumnIndexOrThrow(_cursor, "actionId");
          final int _cursorIndexOfActionName = CursorUtil.getColumnIndexOrThrow(_cursor, "actionName");
          final int _cursorIndexOfCreationTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "creationTimestamp");
          final ActionEntity _result;
          if(_cursor.moveToFirst()) {
            final int _tmpActionId;
            _tmpActionId = _cursor.getInt(_cursorIndexOfActionId);
            final String _tmpActionName;
            if (_cursor.isNull(_cursorIndexOfActionName)) {
              _tmpActionName = null;
            } else {
              _tmpActionName = _cursor.getString(_cursorIndexOfActionName);
            }
            final long _tmpCreationTimestamp;
            _tmpCreationTimestamp = _cursor.getLong(_cursorIndexOfCreationTimestamp);
            _result = new ActionEntity(_tmpActionId,_tmpActionName,_tmpCreationTimestamp);
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
