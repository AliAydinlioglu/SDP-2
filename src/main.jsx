import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import NotFound from './pages/NotFound.jsx';
import './index.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import Layout from './pages/Layout.jsx';
import DashBoard from './pages/DashBoard.jsx';
import Site from './pages/sites/Site.jsx';
import SitesList from './pages/sites/SitesList.jsx';

import PrivateRoute from './components/PrivateRoute.jsx';

import { AuthProvider } from './context/Auth.context.jsx';
import Login from './pages/Login.jsx';
import Logout from './pages/Logout.jsx';

import MachineSmallDetail from './components/machines/MachineSmallDetail.jsx';
import MachinesList from './pages/machines/MachinesList.jsx';
import MachineDetailBig from './pages/machines/MachineDetailBig.jsx';

import AddOrEditMachine from './pages/machines/AddOrEditMachine.jsx';
import AddOrEditSite from './pages/sites/AddOrEditSite.jsx';
import Notifications from './pages/Notifications.jsx';

const router = createBrowserRouter([
  {
    element: <PrivateRoute/>,
    children: [
      {element: <Layout />,
        children: [
          {
            path: '/',
            element: <Navigate replace to='/dashboard' />,
          },
          {
            path: '/dashboard',
            element: <DashBoard />,
          },
          {
            path: '/sites',
            children: [
              {
                index: true,
                element: <SitesList />,
              },
              {
                path: ':id',
                element: <Site />,
                children: [
                  {
                    path: 'machines/:machineId',
                    element: <MachineSmallDetail />,
                  },
                ],
              },
              {
                path: 'add',
                element: <AddOrEditSite />,
              },
              {
                path: 'edit/:id',
                element: <AddOrEditSite />,
              },
            ],
          },
          {
            path: '/machines',
            children: [
              {
                index: true,
                element: <MachinesList />,
              },
              {
                path: ':id',
                element: <MachineDetailBig />,
              },
              {
                path: 'add',
                element: <AddOrEditMachine />,
              },
              {
                path: 'edit/:id',
                element: <AddOrEditMachine />,
              },
            ],
          },
          {
            path: '*',
            element: <NotFound />,
          },
          
          {path: '/notifications', element: <Notifications/>}, // moet nog verandert worden naar meldingen van een user
        ],
      },
    ],
  },
  {
    element: <Login />,
    path: '/login',
  },
  {path: '/logout', element: <Logout/> },
]);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router}/>
    </AuthProvider>
  </StrictMode>,
);