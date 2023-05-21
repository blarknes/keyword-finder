package com.keywordfinder;

import com.keywordfinder.utilities.Router;

public class Application {

    public static void main(String[] args) throws Throwable {
        final var router = new Router();
        router.establishRoutes();
    }

}
