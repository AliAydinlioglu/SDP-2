import { LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip,
  BarChart, Bar, PieChart, Pie, Cell, ScatterChart, Scatter } from 'recharts';

export default function DashBoard() {
  const data = [
    { name: 'Jan', uv: 400, pv: 2400, amt: 2400 },
    { name: 'Feb', uv: 300, pv: 1398, amt: 2210 },
    { name: 'Mar', uv: 200, pv: 9800, amt: 2290 },
    { name: 'Apr', uv: 278, pv: 3908, amt: 2000 },
    { name: 'May', uv: 189, pv: 4800, amt: 2181 },
    { name: 'Jun', uv: 239, pv: 3800, amt: 2500 },
    { name: 'Jul', uv: 349, pv: 4300, amt: 2100 },
  ];

  const pieData = [
    { name: 'Group A', value: 400 },
    { name: 'Group B', value: 300 },
    { name: 'Group C', value: 300 },
    { name: 'Group D', value: 200 },
  ];

  const scatterData = [
    { x: 100, y: 200 },
    { x: 120, y: 100 },
    { x: 170, y: 300 },
    { x: 140, y: 250 },
    { x: 150, y: 400 },
    { x: 110, y: 280 },
  ];

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

  return (
    <div className="dashboard">
      <h1 className="">Dashboard</h1>
      <div className="border-container">
        <div className='graph'>
          <h3>Fake Graph</h3>
          <LineChart width={300} height={200} data={data}>
            <Line type="monotone" dataKey="uv" stroke="#8884d8" />
            <CartesianGrid stroke="#ccc" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
          </LineChart>
        </div>
        <div className='graph'>
          <h3>Fake Graph</h3>
          <LineChart width={300} height={200} data={data}>
            <Line type="monotone" dataKey="uv" stroke="#8884d8" />
            <CartesianGrid stroke="#ccc" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
          </LineChart>
        </div>
        <div className='graph'>
          <h3>Fake Pie Chart</h3>
          <PieChart width={300} height={200}>
            <Pie
              data={pieData}
              cx={150}
              cy={100}
              innerRadius={60}
              outerRadius={80}
              fill="#8884d8"
              paddingAngle={5}
              dataKey="value"
            >
              {pieData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
          </PieChart>
        </div>
        <div className='graph'>
          <h3>Fake Bar Chart</h3>
          <BarChart width={300} height={200} data={data}>
            <CartesianGrid stroke="#ccc" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="pv" fill="#8884d8" />
          </BarChart>
        </div>
        <div className='graph'>
          <h3>Fake Scatter Chart</h3>
          <ScatterChart width={300} height={200}>
            <CartesianGrid />
            <XAxis type="number" dataKey="x" name="stature" unit="cm" />
            <YAxis type="number" dataKey="y" name="weight" unit="kg" />
            <Tooltip cursor={{ strokeDasharray: '3 3' }} />
            <Scatter name="A school" data={scatterData} fill="#8884d8" />
          </ScatterChart>
        </div>
      </div>
    </div>
  );
}