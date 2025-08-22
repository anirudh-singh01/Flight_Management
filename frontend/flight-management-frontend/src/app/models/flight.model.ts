export interface Flight {
  flightId?: number;
  carrierId: number;
  carrierName?: string;
  origin: string;
  destination: string;
  airFare: number;
  seatCapacityBusiness: number;
  seatCapacityEconomy: number;
  seatCapacityExecutive: number;
}

export interface FlightResponse {
  success: boolean;
  message: string;
  data: Flight | Flight[] | null;
}
