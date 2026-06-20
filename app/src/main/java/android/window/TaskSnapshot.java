/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.window;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskSnapshot implements Parcelable {

    protected TaskSnapshot(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int describeContents() {
        throw new UnsupportedOperationException();
    }

    public static final Creator<TaskSnapshot> CREATOR = new Creator<TaskSnapshot>() {
        @Override
        public TaskSnapshot createFromParcel(Parcel in) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TaskSnapshot[] newArray(int size) {
            throw new UnsupportedOperationException();
        }
    };
}