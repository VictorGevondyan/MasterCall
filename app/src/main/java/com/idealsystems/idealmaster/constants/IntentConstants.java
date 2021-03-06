package com.idealsystems.idealmaster.constants;

/**
 * Created by acerkinght on 8/22/16.
 */
public class IntentConstants {
    public static final String ACTION_NEW_ORDER = IntentConstants.class.getName() + "_orderNew";
    public static final String ACTION_ORDER_STARTED = IntentConstants.class.getName() + "_orderStarted";
    public static final String ACTION_ORDER_PAUSED = IntentConstants.class.getName() + "_orderPaused";
    public static final String ACTION_ORDER_FINISHED = IntentConstants.class.getName() + "_orderFinished";
    public static final String ACTION_ORDER_CANCELED = IntentConstants.class.getName() + "_orderCanceled";
    public static final String ACTION_NEW_OFFER = IntentConstants.class.getName() + "_orderNewOffer";

    public static final String EXTRA_ORDER_LIST_TYPE = IntentConstants.class.getName() + "_orderListType";
    public static final String EXTRA_ORDER = IntentConstants.class.getName() + "_order";
    public static final String EXTRA_ENDS_SESSION = IntentConstants.class.getName() + "_extraEndsSession";

    public static final String VALUE_ORDER_LIST_NEW = IntentConstants.class.getName() + "_orderListNew";
    public static final String VALUE_ORDER_LIST_TAKEN = IntentConstants.class.getName() + "_orderListTaken";
    public static final String VALUE_ORDER_LIST_HISTORY = IntentConstants.class.getName() + "_orderListHistory";
   }
