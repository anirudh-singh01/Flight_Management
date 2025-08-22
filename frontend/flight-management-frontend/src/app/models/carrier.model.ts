export interface Carrier {
  carrierId?: number;
  carrierName: string;
  discountPercentage: number;
  refundPercentage: number;
  discountType: DiscountType;
  refundType: RefundType;
  description?: string;
  isActive?: boolean;
}

export enum DiscountType {
  THIRTY_DAYS = 'THIRTY_DAYS',
  SIXTY_DAYS = 'SIXTY_DAYS',
  NINETY_DAYS = 'NINETY_DAYS',
  BULK = 'BULK',
  SILVER = 'SILVER',
  GOLD = 'GOLD',
  PLATINUM = 'PLATINUM'
}

export enum RefundType {
  TWO_DAYS = 'TWO_DAYS',
  TEN_DAYS = 'TEN_DAYS',
  TWENTY_DAYS = 'TWENTY_DAYS'
}

export interface DiscountTypeOption {
  value: DiscountType;
  label: string;
}

export interface RefundTypeOption {
  value: RefundType;
  label: string;
}

export const DISCOUNT_TYPE_OPTIONS: DiscountTypeOption[] = [
  { value: DiscountType.THIRTY_DAYS, label: '30 Days' },
  { value: DiscountType.SIXTY_DAYS, label: '60 Days' },
  { value: DiscountType.NINETY_DAYS, label: '90 Days' },
  { value: DiscountType.BULK, label: 'Bulk' },
  { value: DiscountType.SILVER, label: 'Silver' },
  { value: DiscountType.GOLD, label: 'Gold' },
  { value: DiscountType.PLATINUM, label: 'Platinum' }
];

export const REFUND_TYPE_OPTIONS: RefundTypeOption[] = [
  { value: RefundType.TWO_DAYS, label: '2 Days Before Travel' },
  { value: RefundType.TEN_DAYS, label: '10 Days Before Travel' },
  { value: RefundType.TWENTY_DAYS, label: '20 Days Before Travel' }
];
