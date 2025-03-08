import { LineChart, Line, BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip, ResponsiveContainer,
} from 'recharts';

export default function Chart({data=[], type, title}) {
  return (
    <>
      <h4>{title}</h4>
      <ResponsiveContainer width="95%" height="80%">
        {type === 'line' ? (
          <LineChart data={data}>
            <Line type="monotone" dataKey="uv" stroke="#8884d8" />
            <CartesianGrid stroke="#ccc" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
          </LineChart>
        ) : (
          <BarChart data={data}>
            <Bar dataKey="uv" fill="#8884d8" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
          </BarChart>
        )}
      </ResponsiveContainer>
    </>
  );
}