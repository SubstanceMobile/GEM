/*
 * Copyright 2017 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
	id("substance.SdkPlugin") version "0.0.0"
}

sdk {
	appName = "Gem Player"
	appId = "substance.music"
	suppressPlatformWarning = true

	android {
		compileSdkVersion(26)
		buildToolsVersion("26.0.0")
		androidConfig {
		    defaultConfig {
		        applicationId 'substance.music'
		        targetSdkVersion 26
		        versionName '0.3.0'
		        versionCode 16
		        minSdkVersion 17
		        vectorDrawables.useSupportLibrary = true
		    }

			buildTypes {
		        "internal" { versionNameSuffix(" Internal") }
		        "debug" { versionNameSuffix(" Debug") }
			}

		    dataBinding {
		        enabled = true
			}
		}
		configure {
			repositories {
			    jcenter()
				maven {url "https://jitpack.io"
			}
			dependencies {
			    // Jake Wharton
			    compile("com.jakewharton:butterknife:7.0.1")
			    // Bump Technologies
			    compile("com.github.bumptech.glide:glide:3.7.0")
			    // Daniel Ciao
			    compile("com.github.plusCubed:recycler-fast-scroll:0.3.1")
			    // Paolo Rotolo
			    compile("com.github.paolorotolo:appintro:3.4.0")
			    compile("com.github.paolorotolo:gitty_reporter:1.2.2")
			    // Aidan Follestad
			    val mdVersion = "0.9.4.5"
			    compile("com.github.afollestad.material-dialogs:core:$mdVersion")
			    compile("com.github.afollestad.material-dialogs:commons:$mdVersion")

			    // Mike Penz
			    compile('com.mikepenz:aboutlibraries:5.6.5@aar')

			    // Google
			    val supportVersion = "26.0.0"
			    //compile 'com.android.support:appcompat-v7:23.3.0'
			    //compile 'com.android.support:support-v4:23.3.0'
			    //compile 'com.android.support:recyclerview-v7:23.3.0'
			    //compile 'com.android.support:design:23.3.0'
			    //compile 'com.android.support:cardview-v7:23.3.0'
			    //compile 'com.android.support:palette-v7:23.3.0'
			    //compile 'com.android.support:support-vector-drawable:23.3.0'
			    //compile 'com.android.support:animated-vector-drawable:23.3.0'
			}
		}
	}
}