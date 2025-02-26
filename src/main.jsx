import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import NotFound from './pages/NotFound.jsx';
import './index.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from './pages/Layout.jsx';
import DashBoard from './pages/DashBoard.jsx';
import Site from './pages/sites/Site.jsx';
import SitesList from './pages/sites/SitesList.jsx';
import { AuthProvider } from './context/Auth.context.jsx';
import Login from './pages/Login.jsx';
import Logout from './pages/Logout.jsx';

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
      {path: 'login', element: <Login/>},
      {path: 'logout', element: <Logout/> },
    ],
  },
]);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router}/>
    </AuthProvider>
  </StrictMode>,
);