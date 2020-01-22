
// Feel free to extend this interface
// depending on your app specific config.
export interface EnvConfig {
  API?: string;
  ENV?: string;
  DEBUG_ON?: boolean;
  SUPPORTED_LANGS?:string;
  DEFAULT_LANG?:string;
  CONTEXT_PATH?:string;

  AUTH_JWT_BUFFER?: number;
  AUTH_USERCHECK_BUFFER?: number;

  UI_INPUT_DELAY?: number;
  UI_ALERTCHECK_DELAY?: number;
  UI_BULKSERVICE_DELAY?: number;
  UI_FILTER_CAP?: number;
  UI_FILTER_NO_CAP?: number;
  UI_TABLE_PAGE_SIZE?: number;
  UI_TABLE_PAGE_NUMS?: number;

  UI_ORDER_TOTALS?: string;

  UI_DOC_LINK?: string;
  UI_COPY_NOTE?: string;
  UI_LABEL?: string;
}
