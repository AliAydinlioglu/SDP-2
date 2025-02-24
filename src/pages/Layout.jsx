import { Outlet, ScrollRestoration } from 'react-router-dom';
import Footer from '../components/Footer';
import NavBar from '../components/NavBar';

export default function Layout() {
  return (
    <div>
      <NavBar/>
      <Outlet />
      <ScrollRestoration />
      <Footer/>
    </div>
  );
}
