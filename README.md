# Phial [![Release](https://jitpack.io/v/roshakorost/Phial.svg)](https://jitpack.io/#roshakorost/Phial)

**Phial is an Android library created to simplify communication between QAs and developers.** When bug happens Phial allows to share logs, screenshot, system and build data from a device to developers for simplifying debugging and fixing.

Debug data can be easily extended by using `Key-Values` or providing custom `Attachers`.

Why use it: 
- Quickly dump all needed debug data from the device and share it with developers.
- No need to manually capture screenshots.
- The more recent the snapshot the more valuable the data is.

### Screenshots

![DemoScreenshot][1]

### [Example of attachment][2]

# Enhance Your Attachments
## Custom Keys
Phial allows you to associate arbitrary key/value pairs which are viewable right from the Debug Overlay. These key values pairs are included as JSON with the attachment to be shared.

Setting key is done by calling `Phial.setKey(key, value)`. 

You can provide a category to group your data via  `Phial.category(name)`.

Re-setting the same key will update the value. 

**Examples:**
```
Phial.setKey("currentUser", "admin");
Phial.setKey("currentUser", "user1");
```
This will include key/value **"currentUser" "user1"** in share attachment.

**Note:** Keys associated with different categories are treated as different keys.

**Note:** Setting null will not remove association, but will set null value to the key. In order to remove association use `Phial.removeKey(key)` or `Phial.category(name).removeKey(key)`

If you use `Crashlytics` you might want to integrate Phial key/value with `Crashlytics` as well.
You can do that by implementing `Saver` interface and adding it using `Phial.addSaver(saver)`


## Logging
You can include your application logs into share attachment. If you don’t save your log on disk you can user phial-logging in order to attach html formatted logs with your debug data.

#### Integration
1. Add phial-logger to your dependencies.
2. Add `PhialLogger` to attachers:
```java
final PhialLogger phialLogger = new PhialLogger(app);
PhialOverlay.builder(app)
  .addAttachmentProvider(phialLogger)
  .initPhial();
```
`phialLogger.log(priority, tag, message,throwable)` will dump log to the file that will be included into share attachment.

**Note:** it is recommended to use some logging facade instead of calling phialLogger.log(priority, tag, message,throwable) manually. Check [Timber Integration Example.][3]

If you already store your logs on dive you can use attachers in order to add them to share attachement. See next section on how to implement custom attachers.



[1]:https://raw.githubusercontent.com/roshakorost/Phial/develop/art/screenshot_demo.gif
[2]:art/data_M11D01_H15_58_53/
[3]:sample/src/qa/java/com/mindcoders/phial/sample/ApplicationHook.java

## Custom attachers

Phial allows to include your custom data into share attachments. For example,  you might want to include SQLite database, logs or SharedPreferences files  from the device in order to investigate an issue.

If you need to include a file to share with the attachment you can use `SimpleFileAttacher(File file)` or `SimpleFileAttacher(Collection<File> files).
If the file is a directory all files from it will be attached as well.

Example:
```java
PhialOverlay.builder(app)
.addAttachmentProvider(new SimpleFileAttacher(sqlLiteFile))
.initPhial();
```
In case you want to include some information that is not persisted to file, you can use 
Attacher or ListAttacher.

Currently Attacher API works only with files so when `provideAttachment` is called you should dump data to the temporary file and return it. When `onAttachmentNotNeeded` is called the temporary file can be deleted (see SharedPreferencesAttacher in the sample app or KVAttacher for an example).

## Share
By default Phial’s share menu only shows installed applications that can handle zip attachments. However you might want to include your own share options e.g. creating Jira Issue or posting to specific slack channel.

### Phial-Jira
Phial-jira allows you to login to your Jira and create an issue with debug data attached to it.
Login page will be shown only the first time. After that saved credentials will be used.
```java
final Shareable jiraShareable = new JiraShareableBuilder(app)
       .setBaseUrl(url) //Jira url
       .setProjectKey(projectKey)	//project key
       .build();

PhialOverlay.builder(app)
	.addShareable(jiraShareable)
	.initPhial();

```
**Note:** since credentials are not stored securely  it’s recommended to use Phial only in internal/debug builds.

### Shareable
You can add your own share options by implementing `Shareable`.
When user selects your share option `void share(ShareContext shareContext, File zippedAttachment, String message);` will be called.
You should call either `shareContext.onSuccess()`, `shareContext.onFail(message)` or `shareContext.onCancel()` when share is finished.
`ShareContext` also provides interface for adding your UI elements in case you need authorization or some extra fields. See `JiraShareable` from `phial-jira` as an example implementation.

## Custom overlay pages
You can add custom pages that will be available in the overlay.
To do this provide your instance of Page class to the PhialOverlay.Builder

```java
PhialOverlay.Builder(app)
.addPage(customPage)
```
```java
Page customPage = new Page(
“customPage”, // unique page id
R.drawable.ic_custom_page, // page icon resource
“Custom page”, // page title
customPageFactory, // implementation of PageViewFactory 
);
```

`PageViewFactory` is responsible for instantiating your page view.
Your page view should be implemented as a subclass of `android.view.View` and implement `PageView` interface.

Currently `PageView` is only used for overriding back navigation.
If your page view needs more than a common navigation flow (device back button minimized the overlay) you can implement this logic in `PageView#onBackPressed` method.
