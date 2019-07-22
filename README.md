[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/I2I8ZPRJ)

# ContactFetcher
An app to read all contacts with emails and mobile numbers. It also supports multiple emails and mobile numbers.

**Current version:**  <a href='https://bintray.com/raghavsatyadev/Maven/ContactFetcher/_latestVersion'><img src='https://api.bintray.com/packages/raghavsatyadev/Maven/ContactFetcher/images/download.svg'></a>

# Setup
To use this library your minSdkVersion must be >= 16.

In the build.gradle of your app module add:

```gradle
    dependencies {
        implementation 'com.rocky.contactfetcher:contactfetcher:x.x.x'
    }
```

# Example

```java

    ContactFetcher.getContacts(this, new ContactListener<Contact>() {
          @Override
          public void onNext(Contact contact) {
              // add contacts to list
          }

          @Override
            public void onError(Throwable error) {
              // log the error
          }

          @Override
          public void onComplete() {
             // process complete
          }
      });

```

in onRequestPermissionsResult() method

```java

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        ContactFetcher.resolvePermissionResult(this, requestCode, permissions, grantResults);
    }

```

This library is highly inspired by **RXContacts :**  https://github.com/UlrichRaab/rx-contacts
