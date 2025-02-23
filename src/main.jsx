import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import NotFound from './pages/NotFound.jsx';
import './index.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import Layout from './pages/Layout.jsx';
import DashBoard from './pages/DashBoard.jsx';
import Site from './pages/Site.jsx';

const router = createBrowserRouter([
  {
    element: <Layout/>,
    children:[
      {
        path: '/',
        element: <App />,
      },
      {
        path: 'dashboard',
        element: <DashBoard/>,
      },
      {path: '*',
        element: <NotFound/>,
      },
      {
        path: 'site',
        children: [
          {
            path: ':id',
            element: <Site/>,
          },
        ],
      },
    ],
  },
]);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
);
