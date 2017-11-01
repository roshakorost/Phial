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



[1]:https://raw.githubusercontent.com/roshakorost/Phial/develop/art/screenshot_demo.gif
[2]:https://github.com/roshakorost/Phial/tree/develop/art/data_M11D01_H15_58_53

## Enhance Your Attachments
### Custom Keys
Phial allows you to associate arbitrary key/value pairs which are viewable right from the Debug Overlay. These key values pairs are included as JSON with the attachment to be shared.

Setting key is done by calling `Phial.setKey(key, value)`. 

You can provide a category to group your data via  `Phial.category(name)`.

Re-setting the same key will update the value. 

**Examples:**
```
Phial.setKey(“currentUser”, “admin”);
Phial.setKey(“currentUser”, “user1);
```
This will include key/value `currentUser` `user1` in share attachment.

**Note:** Keys associated with different categories are treated as different keys.

**Note:** Setting null will not remove association, but will set null value to the key. In order to remove association use `Phial.removeKey(key)` or `Phial.category(name).removeKey(key)`

If you use `Crashlytics` you might want to integrate Phial key/value with `Crashlytics` as well.
You can do that by implementing `Saver` interface and adding it using `Phial.addSaver(saver)`

