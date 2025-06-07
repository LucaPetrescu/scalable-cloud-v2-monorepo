import { Injectable, OnModuleInit } from '@nestjs/common';
import * as mongoose from 'mongoose';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class DatabaseService implements OnModuleInit {
  constructor(private configService: ConfigService) {}

  async onModuleInit() {
    await this.connectToDatabase();
  }

  async connectToDatabase() {
    try {
      const dbUri = this.configService.get<string>('MONGODB_URI');
      await mongoose.connect(`${dbUri}/products`);
      console.log('Successfully connected to MongoDB:', dbUri);
    } catch (error) {
      console.error('Error connecting to MongoDB', error);
    }
  }
}
