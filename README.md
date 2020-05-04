# LayoutVerifier

![](https://github.com/dmitry-zaitsev/LayoutVerifier/workflows/Android%20Master%20CI/badge.svg)

Test Android layouts without device. LayoutVerifier is a Robolectric-based testing library which 
makes sure your Android screens are never broken again.

**What's in the box:**
- Library which makes sure that your Views are where they are supposed to be on the screen.
- No need for an emulator.
- No need for a build plugin or complex initial setup. Single library is all it takes.
- As little as 3 lines to write a test.
- Similar to Screenshot tests. Minus screenshots.

## Usage

Get started by adding LayoutVerifier to your test dependencies and do some minimal configuration:

```
android {

    testOptions {
      unitTests {
          includeAndroidResources = true
      }
    }

}

dependencies {
    
    // Make sure Robolectric is included

    testImplementation 'com.redapparat:layoutverifier:1.0.0'
}

```

Then, simply write a JUnit test:

```
LayoutVerifier.Builder(getApplicationContext)
    .layout(R.layout.myLayout)
    .screenSize(600, 800)       // optional: screen size in DP
    .views(                     // optional: those views which you care about, otherwise compare all views
        R.id.buyButton, 
        R.id.totalPrice
    )
    .match("test_case_name")    // optional: arbitraty test case name which is unique to your module
```

That is it. On the first run the test will pass and save a pre-recorded version of the layout to 
disk. Make sure to keep this file and include it into your commits. 

## How it works

### Concept

LayoutVerifier captures essential information about your View (i.e. elements position, 
text content) and then compares it to a pre-recorded "standard" version of the layout. If there is 
no pre-recorded version available, current state of the view is assumed to be correct and then 
recorded as a new standard for future comparisons.

If you have worked with Screenshot-tests before all of that might sound familiar to you. 
LayoutVerifier uses a very similar concept but instead of comparing screenshots it compares data.

### How views are compared

The following view features (aka attrbiutes) are compared:

- Position (left, top, right, bottom)
- Text (children of a `TextView`, including `EditText` and `Button`)
- Enabled state
- Clickable state

Furthermore, new features can easily be added by providing a custom implementation of 
`FeatureExtractor`. 

## License

```
Copyright 2020 LayoutVerifier

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
``` 
