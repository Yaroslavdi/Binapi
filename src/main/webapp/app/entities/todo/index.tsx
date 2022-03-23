import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TODO from './todo';
import TODODetail from './todo-detail';
import TODOUpdate from './todo-update';
import TODODeleteDialog from './todo-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TODOUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TODOUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TODODetail} />
      <ErrorBoundaryRoute path={match.url} component={TODO} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TODODeleteDialog} />
  </>
);

export default Routes;
