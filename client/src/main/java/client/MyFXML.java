/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.google.inject.Injector;

import client.utils.I18nService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Utility class that loads JavaFX FXML files while wiring their controllers
 * with a {@link Injector} (Guice).
 */
public class MyFXML {

    private Injector injector;
    private I18nService i18nService;

    /**
     * Creates a new {@code MyFXML} instance.
     *
     * @param injector the Guice injector that will provide controller instances
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
        this.i18nService = new I18nService();
    }

    /**
     * Loads an FXML file and returns its controller together with the root node.
     *
     * <p>The {@code parts} argument is interpreted as a relative path on the
     * class‑path.  For example, {@code load(RecipeOverviewCtrl.class, "client","scenes",
     * "RecipeOverview.fxml")} will look for {@code client/scenes/RecipeOverview.fxml}.
     *
     * @param <T>   type of the controller to be returned
     * @param c     the class object of the controller (used only for typing)
     * @param parts path segments that form the resource location on the class‑path
     * @return a {@link Pair} containing the controller instance and its root node
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public <T> Pair<T, Parent> load(Class<T> c, String... parts) {
        try {
            ResourceBundle bundle = i18nService.getBundle();
            var loader = new FXMLLoader(getLocation(parts), bundle, null,
                    new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves the class‑path location of an FXML file from path segments.
     *
     * @param parts path segments that form the resource location
     * @return a {@link URL} pointing to the resource, or {@code null} if not found
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    /**
     * Custom {@link BuilderFactory} and {@link Callback} that delegate object
     * creation to the Guice injector.
     */
    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}