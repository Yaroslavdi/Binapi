import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITODO, defaultValue } from 'app/shared/model/todo.model';

export const ACTION_TYPES = {
  FETCH_TODO_LIST: 'tODO/FETCH_TODO_LIST',
  FETCH_TODO: 'tODO/FETCH_TODO',
  CREATE_TODO: 'tODO/CREATE_TODO',
  UPDATE_TODO: 'tODO/UPDATE_TODO',
  DELETE_TODO: 'tODO/DELETE_TODO',
  RESET: 'tODO/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITODO>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type TODOState = Readonly<typeof initialState>;

// Reducer

export default (state: TODOState = initialState, action): TODOState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TODO_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TODO):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_TODO):
    case REQUEST(ACTION_TYPES.UPDATE_TODO):
    case REQUEST(ACTION_TYPES.DELETE_TODO):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_TODO_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TODO):
    case FAILURE(ACTION_TYPES.CREATE_TODO):
    case FAILURE(ACTION_TYPES.UPDATE_TODO):
    case FAILURE(ACTION_TYPES.DELETE_TODO):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TODO_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TODO):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_TODO):
    case SUCCESS(ACTION_TYPES.UPDATE_TODO):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_TODO):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/todos';

// Actions

export const getEntities: ICrudGetAllAction<ITODO> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_TODO_LIST,
  payload: axios.get<ITODO>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<ITODO> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TODO,
    payload: axios.get<ITODO>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ITODO> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TODO,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITODO> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TODO,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITODO> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TODO,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
