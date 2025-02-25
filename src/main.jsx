import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import NotFound from './pages/NotFound.jsx';
import './index.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from './pages/Layout.jsx';
import DashBoard from './pages/DashBoard.jsx';
import Site from './pages/sites/Site.jsx';
import SitesList from './pages/sites/SitesList.jsx';

const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      {
        path: '/',
        element: <Navigate replace to='/dashboard' />,
      },
      {
        path: 'dashboard',
        element: <DashBoard />,
      },
      {
        path: 'sites',
        element: <SitesList />,
      },
      {
        path: 'sites/:id',
        element: <Site />,
      },
      {
        path: '*',
        element: <NotFound />,
      },
    ],
  },
]);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
);
