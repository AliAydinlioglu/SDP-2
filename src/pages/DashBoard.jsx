import DashboardUser from '../components/Dashboard/DashBoardUser';
import DashboardAdmin from '../components/Dashboard/DashboardAdmin';
import { KPI_DATA } from '../api/mock_data';

export default function DashBoard() {

  const admin = true;

  return (
    <div className="dashboard">
      {admin 
        ? <DashboardAdmin KPIs={KPI_DATA} />
        : <DashboardUser KPIs={KPI_DATA} />
      }
    </div>
  );
}
