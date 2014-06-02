Wattitude - app
===============

Setup
-----

Open in Android-studio.

The server address is defined in utils/Global.java. Set the String URL to the correct address
where the server is currently hosted.

Libraries
---------

* FacebookSDK - Facebook session data
* Spinnerwheel - Spinning view
* pagerslidingtabstrip - tabs
* Volley - http requests
* Jackson - Marshalling
* Progressfragment - Fragment with progress state
* android-segmented - Segmented radiobuttons
* aChartEngine - Charting library

Contribution
------------

The app has folders for
* Fragments
    
    All the different Fragment views

* Activities

    Activities including the login Activity and DrawerActivity that is the main app activity.

* Adapters
* Databases

    All data is added in a database, and the database is wrapped around a ContentProvider.
    The EnergyContract.java defines the data URI's. DeviceModel and EnergyUsageModel wraps around
    their respective java bean models (found in model folder). They provide SQL statements for insertion
    and deletion, and other methods that help using the data. 
    
    The EnergyOpenHelper wraps around the SQLiteDatabase, and updates the DB if there are new changes 
    in the models. 
    
    The EnergyProvider implements the actual ContentProvider, and specifies all the allowed CRUD 
    operations on the data.
     
    The EnergyDataSource class is a low hanging wrapper around the ContentProvider for easily specifying 
    typical queries.

* Models
    
    All data models used.
    
* Services
* Utils
* Preferences

    Wrappers around SharedPreferences.

* Dialogs



