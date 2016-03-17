package unidue.lib.model;

import unidue.lib.model.auto._ReserveCollectionsDatamap;

public class ReserveCollectionsDatamap extends _ReserveCollectionsDatamap {

    private static ReserveCollectionsDatamap instance;

    private ReserveCollectionsDatamap() {}

    public static ReserveCollectionsDatamap getInstance() {
        if(instance == null) {
            instance = new ReserveCollectionsDatamap();
        }

        return instance;
    }
}
