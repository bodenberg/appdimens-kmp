/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens-kmp.git
 *
 * Library: AppDimens KMP — Custom scaling strategies
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
package com.appdimens.kmp.custom

internal actual fun platformDeviceClass(uiMode: Int, smallestWidthDp: Float): DeviceClass =
    if ((uiMode and 0x0F) == 1) DeviceClass.DESKTOP else DeviceClass.fromAndroidUiMode(uiMode, smallestWidthDp)
