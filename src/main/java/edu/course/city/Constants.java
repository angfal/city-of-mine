package edu.course.city;

public final class Constants {

	private Constants() {
        // constants class
	}

    // Map constants <<<
	public static final String MAP_TYPE_ROADMAP = "ROADMAP";

    public static final String MAP_DEFAULT_TYPE = MAP_TYPE_ROADMAP;

	public static final String MAP_DEFAULT_COORDINATES = "53.666667, 23.831667";

	public static final int MAP_DEFAULT_ZOOM = 13;

    public static final String MAP_DEFAULT_PLACE_ICON = "/resources/images/map/place.png";

    public static final String MAP_DYNAMIC_PLACE_ICON = "/javax.faces.resource/dynamiccontent.properties.jsf?imageId=%s";

    public static final String MAP_ROUTE_COLOR = "black";
    // >>>

    // Tree node types <<<
    public static final String TREE_NODE_TYPE_ROOT = "ROOT";

    public static final String TREE_NODE_TYPE_GLOBAL_GROUP = "GLOBAL_GROUP";

    public static final String TREE_NODE_TYPE_LOCAL_GROUP = "LOCAL_GROUP";

    public static final String TREE_NODE_TYPE_GLOBAL_PLACE = "GLOBAL_PLACE";

    public static final String TREE_NODE_TYPE_LOCAL_PLACE = "LOCAL_PLACE";

    public static final String TREE_NODE_TYPE_LOCAL_ROUTE = "LOCAL_ROUTE";

    public static final String TREE_NODE_TYPE_FAVORITE = "FAVORITE";
    // >>>

    // Icon sizes <<<
    public static final int TREE_ICON_SIZE = 40;
    // >>>
}
