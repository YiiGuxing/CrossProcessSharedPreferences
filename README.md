# AndroidRemoteSharedPreferences
AndroidRemoteSharedPreferences


Gradle
------
```groovy
compile 'cn.tinkling.prefs:remote-shared-preferences:1.0.1'
```


用法
----

**1.通过Bundle**

- 远程进程:

  ```java
  SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
  RemoteSharedPreferences rsp = new RemoteSharedPreferences(preferences);
  RemoteSharedPreferencesDescriptor descriptor = rsp.getSharedPreferencesDescriptor();
  Bundle bundle = new Bundle();
  // If API level >= 18.
  // bundle.putBinder("preferences", rsp);
  bundle.putParcelable("preferences", descriptor);
  // ... 将bundle发送到本地进程
  ```

- 本地进程:

  ```java
  // ...
  RemoteSharedPreferencesDescriptor d = bundle.getParcelable("preferences");
  SharedPreferences preferences = new RemoteSharedPreferencesProxy(d);
  // ...

  // If API level >= 18.
  IBinder binder = bundle.getBinder("preferences");
  IRemoteSharedPreferences remotePrefs = RemoteSharedPreferences.asInterface(binder);
  SharedPreferences preferences = new RemoteSharedPreferencesProxy(remotePrefs);
  // ...
  ```

**2.直接通过Binder**

- 远程进程:

  ```java
  public class RemoteService extends Service {

      public static final String ACTION_REMOTE_SHARED_PREFERENCES = "remote_shared_preferences";

      // ...

      @Override
      public IBinder onBind(Intent intent) {
          if (ACTION_REMOTE_SHARED_PREFERENCES.equals(intent.getAction())) {
              SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
              return new RemoteSharedPreferences(preferences);
          }

          return null;
      }
  }
  ```

- 本地进程:

  ```java
  ServiceConnection conn = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
          // 与远程SharedPreferences完成连接
          IRemoteSharedPreferences remotePrefs = RemoteSharedPreferences.asInterface(service);
          SharedPreferences preferences = new RemoteSharedPreferencesProxy(remotePrefs);
          // ...
      }
      // ...
  };

  // ...
  Intent binder = new Intent(this, RemoteService.class);
  binder.setAction(RemoteService.ACTION_REMOTE_SHARED_PREFERENCES);
  bindService(binder, conn, BIND_AUTO_CREATE);
  ```

**3.通过AIDL**

- aidl:

  ```java
  import cn.tinkling.prefs.IRemoteSharedPreferences;

  interface IMyAidlInterface {
      IRemoteSharedPreferences getRemoteSharedPreferences();
  }
  ```

- 远程进程:

  ```java
  class MyAidlInterfaceImpl extends IMyAidlInterface.Stub {
    	@Override
  	public IRemoteSharedPreferences getRemoteSharedPreferences() throws RemoteException {
      	SharedPreferences preferences = RemoteService.this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
          return new RemoteSharedPreferences(preferences);
  	}
  }
  ```

- 本地进程:

  ```java
  // IMyAidlInterface myAidlInterface = ...
  IRemoteSharedPreferences remotePrefs = myAidlInterface.getRemoteSharedPreferences();
  SharedPreferences preferences = new RemoteSharedPreferencesProxy(remotePrefs);
  // ...
  ```



#### [**使用示例**](https://github.com/YiiGuxing/AndroidRemoteSharedPreferences/tree/master/sample/src/main/java/cn/tinkling/prefs/sample)