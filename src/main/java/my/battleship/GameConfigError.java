/*
 * Copyright (c) 2014,2017 IBM Corporation
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
package my.battleship;

/**
 * Purpose: Create a specific Battleship game configuration error type. This is
 * an "unchecked" exception that will occur if the game setup and configuration
 * cannot be completed or an invalid configuration is detected.
 */
@Copyright(Copyright.c2014)
public class GameConfigError extends Error {

    private static final long serialVersionUID = -8581312946711512688L;

    public GameConfigError() {
        super();
    }

    public GameConfigError(String message) {
        super(message);
    }

    public GameConfigError(String message, Throwable cause) {
        super(message, cause);
    }

}
