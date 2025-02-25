import { useParams } from 'react-router';
import {SITE_DATA, MACHINE_DATA }from '../../api/mock_data';
import SiteDetail from '../../components/sites/SiteDetail';
import MachineTabel from '../../components/machines/MachineTabel';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

export default function Site(){
  const { id } = useParams();
  const idNum = Number(id);

  const site = SITE_DATA.find((s) => s.id === idNum);
  const machines = MACHINE_DATA.filter((m) => m.site_id === idNum);
  console.log(site);
  console.log(machines);

  const data = [
    { name: 'Machine A', value: 400 },
    { name: 'Machine B', value: 300 },
    { name: 'Machine C', value: 200 },
    { name: 'Machine D', value: 278 },
    { name: 'Machine E', value: 189 },
  ];
  
  return (
    <div className="container">
      <SiteDetail site={site} />
      <MachineTabel machines={machines} />
      <div style={{ width: '50%', height: 300 }}>
        <ResponsiveContainer>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="value" stroke="#8884d8" />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}